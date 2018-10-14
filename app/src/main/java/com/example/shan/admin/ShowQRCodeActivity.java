package com.example.shan.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.shan.admin.misc.CustomProgressDialog;
import com.example.shan.admin.misc.CustomSnackbar;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ShowQRCodeActivity extends AppCompatActivity implements View.OnClickListener {


    private RelativeLayout parent;

    private Toolbar toolbar;

    private Button btnDownload;
    private Button btnMail;

    private String value;

    private Bitmap bitmap;

    private ImageView ivQRCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_qrcode);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        parent = (RelativeLayout) findViewById(R.id.parent);

        btnDownload = (Button) findViewById(R.id.btn_download);
        btnMail = (Button) findViewById(R.id.btn_mail);

        ivQRCode = (ImageView) findViewById(R.id.iv_qr_code);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                onBackPressed();
            }
        });
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_back_white));

        SharedPreferences pref = getSharedPreferences("userDetail", 0);
        value = pref.getString("qrCodeValue", null);

        if (value != null)
            generateQRCode(value);

        btnMail.setOnClickListener(this);
        btnDownload.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download:
                final ProgressDialog pd = CustomProgressDialog.ctor(ShowQRCodeActivity.this);
                pd.show();
                try {
                    String path = Environment.getExternalStorageDirectory().toString();
                    OutputStream fOut = null;
                    File file = new File(path, value + ".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                    fOut = new FileOutputStream(file);

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                    fOut.flush(); // Not really required
                    fOut.close(); // do not forget to close the stream

                    MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

                    CustomSnackbar.createSnackbarRed("QR code downloaded successfully", parent, ShowQRCodeActivity.this);

                } catch (FileNotFoundException e) {
                    System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
                } finally {
                    pd.dismiss();
                }
                break;
            case R.id.btn_mail:
                try {
                    String path = Environment.getExternalStorageDirectory().toString();
                    OutputStream fOut = null;
                    File file = new File(path,"QRCode.jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                    fOut = new FileOutputStream(file);

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                    fOut.flush();
                    fOut.close();

                    MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    //intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"email@example.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "QR code");
                    intent.putExtra(Intent.EXTRA_TEXT, "Hello, " +
                            "PFA for QR Code");
                    if (!file.exists() || !file.canRead()) {
                        Toast.makeText(this, "Attachment Error", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    //Uri uri = Uri.parse("file://" + file);
                    Uri uri = Uri.fromFile(file);

                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(intent, "Send email..."));
                } catch (FileNotFoundException e) {
                    System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
                }
                break;
        }
    }

    private void generateQRCode(String value) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(value, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            ivQRCode.setImageBitmap(bitmap);


        } catch (WriterException e) {
            System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
        }
    }
}
