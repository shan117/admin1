package com.example.shan.admin;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.shan.admin.misc.CustomProgressDialog;
import com.example.shan.admin.misc.CustomSnackbar;
import com.google.android.gms.internal.et;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Calendar;

public class GenerateQRCodeActivity extends AppCompatActivity {

    private RelativeLayout parent;

    private Toolbar toolbar;
    private String filepath = "Security";

    private Button btnSubmit;

    private EditText etValue;

    private static final String QR_CODE_IMAGE_PATH = "./MyQRCode.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        parent = (RelativeLayout) findViewById(R.id.parent);

        etValue = (EditText) findViewById(R.id.et_value);

        btnSubmit = (Button) findViewById(R.id.btn_submit);

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

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etValue.getText().toString().isEmpty() || etValue.getText().toString().length() == 0) {
                    CustomSnackbar.createSnackbarRed("Please enter QR code value", parent, GenerateQRCodeActivity.this);
                } else{
                    generateQRCode(etValue.getText().toString());
                }
            }
        });
    }

    private void generateQRCode(String value)
    {
        final ProgressDialog pd= CustomProgressDialog.ctor(GenerateQRCodeActivity.this);
        pd.show();
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(value, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            String path = Environment.getExternalStorageDirectory().toString();
            OutputStream fOut = null;
            File file = new File(path, value+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
            fOut = new FileOutputStream(file);

            bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream

            MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
            CustomSnackbar.createSnackbarRed("QR Code generated succcessfully", parent, GenerateQRCodeActivity.this);

        } catch (WriterException e) {
            System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
        } catch(FileNotFoundException e){
            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
        }catch (IOException e) {
            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
        }finally {
            pd.dismiss();
        }
    }

}
