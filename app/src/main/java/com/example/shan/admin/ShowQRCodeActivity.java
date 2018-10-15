package com.example.shan.admin;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

    private static final int RC_HANDLE_WRITE_PERM = 2;

    private boolean isMail=false;

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
                isMail=false;
                int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (rc == PackageManager.PERMISSION_GRANTED) {
                    downloadQRCode();
                } else {
                    requestWritePermission();
                }
                break;
            case R.id.btn_mail:
                isMail=true;
                int rc1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (rc1 == PackageManager.PERMISSION_GRANTED) {
                    emailQRCode();
                } else {
                    requestWritePermission();
                }
                break;
        }
    }

    private  void requestWritePermission(){
        Log.w("ShowQRCodeActivity", "Write permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_WRITE_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_WRITE_PERM);
            }
        };

        findViewById(R.id.parent).setOnClickListener(listener);
        Snackbar.make(parent,"Write access needed to save the QR Code",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", listener)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_WRITE_PERM) {
            Log.d("ShowQRCodeActivity", "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("ShowQRCodeActivity", "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource

            if(isMail)
            {
                emailQRCode();
            }else{
                downloadQRCode();
            }

            return;
        }

        Log.e("ShowQRCodeActivity", "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Persmission Denied")
                .setMessage("This application cannot run because write permission is not granted")
                .setPositiveButton("OK", listener)
                .show();
    }


    private void emailQRCode(){
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
            intent.setData(Uri.parse("mailto:"));
            intent.setType("message/rfc822");
            //intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"email@example.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "QR code");
            intent.putExtra(Intent.EXTRA_TEXT, "Hello, " +
                    "PFA for QR Code");
            if (!file.exists() || !file.canRead()) {
                Toast.makeText(this, "Attachment Error", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            Uri uri = Uri.fromFile(file);

            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, "Send email..."));
        } catch (FileNotFoundException e) {
            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
        }
    }

    private void downloadQRCode(){
        try {
            String path = Environment.getExternalStorageDirectory().toString();
            OutputStream fOut = null;
            File file = new File(path, value + ".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
            fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream

            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("QR code downloaded successfully, you can view it in Gallery.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        } catch (FileNotFoundException e) {
            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
        } finally {

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
