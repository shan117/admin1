package com.example.shan.admin;

import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shan.admin.misc.CustomSnackbar;

public class AddLocationActivity extends AppCompatActivity {

    private EditText etLocation;
    private EditText etName;

    private Toolbar toolbar;

    private Button btnSubmit;

    private RelativeLayout parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        toolbar=(Toolbar) findViewById(R.id.toolbar);

        parent=(RelativeLayout) findViewById(R.id.parent);

        etLocation = (EditText) findViewById(R.id.et_location);
        etName = (EditText) findViewById(R.id.et_name);

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
                if(etName.getText().toString()==null || etName.getText().toString().isEmpty())
                {
                    CustomSnackbar.createSnackbarRed("Please enter name of location",parent,AddLocationActivity.this);
                }
                else
                {
                    addLocation();
                }
            }
        });

    }

    private void addLocation(){

    }
}
