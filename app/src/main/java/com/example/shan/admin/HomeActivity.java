package com.example.shan.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView ivAdd;
    ImageView ivAnalyse;

    TextView tvRole;
    TextView tvName;
    TextView tvEmail;
    TextView tvCompany;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvName=(TextView) findViewById( R.id.tv_name);
        tvEmail=(TextView) findViewById( R.id.tv_email);
        tvCompany=(TextView) findViewById( R.id.tv_company);
        tvRole=(TextView) findViewById( R.id.tv_role);

        ivAdd=(ImageView) findViewById(R.id.iv_add);
        ivAnalyse=(ImageView) findViewById(R.id.iv_analyse);

        ivAdd.setOnClickListener(this);
        ivAnalyse.setOnClickListener(this);


        SharedPreferences pref = getSharedPreferences("userDetail", 0);
        Gson gson = new Gson();
        String json = pref.getString("user", "");
        User user = gson.fromJson(json, User.class);

        tvName.setText(user.getName());
        tvEmail.setText(user.getEmail());
        tvCompany.setText(user.getCompany());
        tvRole.setText(user.getRole());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.iv_add:
                startActivity(new Intent(HomeActivity.this, AddUserActivity.class));
                break;

            case R.id.iv_analyse:
                startActivity(new Intent(HomeActivity.this, AddUserActivity.class));
                break;
        }

    }
}
