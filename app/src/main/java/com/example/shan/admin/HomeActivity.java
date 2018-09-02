package com.example.shan.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private ImageView ivAdd;
    private ImageView ivAnalyse;
    private ImageView ivLogout;

    private TextView tvRole;
    private TextView tvName;
    private TextView tvEmail;
    private TextView tvCompany;
    private TextView tvAdd;

    GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvName=(TextView) findViewById( R.id.tv_name);
        tvEmail=(TextView) findViewById( R.id.tv_email);
        tvCompany=(TextView) findViewById( R.id.tv_company);
        tvRole=(TextView) findViewById( R.id.tv_role);
        tvAdd=(TextView) findViewById( R.id.tv_add);

        ivAdd=(ImageView) findViewById(R.id.iv_add);
        ivAnalyse=(ImageView) findViewById(R.id.iv_analyse);
        ivLogout=(ImageView) findViewById(R.id.iv_logout);

        ivAdd.setOnClickListener(this);
        ivAnalyse.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions. DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SharedPreferences pref = getSharedPreferences("userDetail", 0);
        Gson gson = new Gson();
        String json = pref.getString("user", "");
        User user = gson.fromJson(json, User.class);

        tvName.setText(user.getName());
        tvEmail.setText(user.getEmail());
        tvCompany.setText(user.getCompany());
        tvRole.setText(user.getRole());

        if(user.getRole().contentEquals("superAdmin"))
        {
            tvAdd.setText("Add Superuser");
        }else if(user.getRole().contentEquals("superUser"))
        {
            tvAdd.setText("Add User");
        }else {
            tvAdd.setText("Add Supervisor");
        }
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

            case R.id.iv_logout:
                signOut();
                break;
        }

    }
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        boolean isLogoutSuccess = status.isSuccess();
                        if (isLogoutSuccess) {
                            Toast.makeText(HomeActivity.this, "Successfully logged out", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(HomeActivity.this, MainActivity.class));
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
