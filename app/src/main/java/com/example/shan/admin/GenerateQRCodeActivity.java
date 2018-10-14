package com.example.shan.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
                    SharedPreferences pref = getSharedPreferences("userDetail", 0);
                    SharedPreferences.Editor prefsEditor = pref.edit();
                    prefsEditor.putString("qrCodeValue",etValue.getText().toString());
                    prefsEditor.commit();
                    startActivity(new Intent(GenerateQRCodeActivity.this, ShowQRCodeActivity.class));
                }
            }
        });
    }



}
