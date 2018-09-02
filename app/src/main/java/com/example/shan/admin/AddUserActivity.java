package com.example.shan.admin;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shan.admin.misc.CustomProgressDialog;
import com.example.shan.admin.misc.CustomSnackbar;
import com.example.shan.admin.misc.Helper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

public class AddUserActivity extends AppCompatActivity {

    private TextInputLayout ilName;
    private TextInputLayout ilEmail;
    private TextInputLayout ilSVName;
    private TextInputLayout ilSVPswd;
    private TextInputLayout ilSVId;

    private EditText etEmail;
    private EditText etName;
    private EditText supervisorUserId, supervisorPwd, supervisorName;

    private TextView tvTitle;

    String encryptedEmail;
    String encryptedMailLoggedinUser;

    private Toolbar toolbar;

    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        toolbar=(Toolbar) findViewById(R.id.toolbar);

        tvTitle=(TextView) findViewById(R.id.toolbar_title);

        ilName=(TextInputLayout) findViewById(R.id.il_name);
        ilEmail=(TextInputLayout) findViewById(R.id.il_email);
        ilSVId=(TextInputLayout) findViewById(R.id.il_sv_id);
        ilSVName=(TextInputLayout) findViewById(R.id.il_sv_name);
        ilSVPswd=(TextInputLayout) findViewById(R.id.il_sv_pswd);

        etEmail = (EditText) findViewById(R.id.et_email);
        etName = (EditText) findViewById(R.id.et_name);
        supervisorUserId = (EditText) findViewById(R.id.et_sv_id);
        supervisorPwd = (EditText) findViewById(R.id.et_sv_pswd);
        supervisorName = (EditText) findViewById(R.id.et_sv_name);

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

        SharedPreferences pref = getSharedPreferences("userDetail", 0);
        Gson gson = new Gson();
        String json = pref.getString("user", "");
        final User user = gson.fromJson(json, User.class);

        if (user.getRole().equalsIgnoreCase("user")) {
            ilSVName.setVisibility(View.VISIBLE);
            ilSVId.setVisibility(View.VISIBLE);
            ilSVPswd.setVisibility(View.VISIBLE);
            ilEmail.setVisibility(View.GONE);
            ilName.setVisibility(View.GONE);
            tvTitle.setText("Add Supervisor");
        } else {
            ilSVId.setVisibility(View.GONE);
            ilSVPswd.setVisibility(View.GONE);
            ilSVName.setVisibility(View.GONE);
            ilName.setVisibility(View.VISIBLE);
            ilEmail.setVisibility(View.VISIBLE);

            if (user.getRole().equalsIgnoreCase("superAdmin")) {
                tvTitle.setText("Add Superuser");
            }else{
                tvTitle.setText("Add user");
            }
        }


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                encryptedMailLoggedinUser = Helper.stringToBase64(user.getEmail());
                if (user.getRole().equalsIgnoreCase("user")) {
                    //Here encryptedEmail= encrypted supervisorUserId
                    encryptedEmail = Helper.stringToBase64(supervisorUserId.getText().toString());

                    final ProgressDialog pd = CustomProgressDialog.ctor(AddUserActivity.this);
                    pd.show();
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("friends").child(encryptedEmail);
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {

                                //new supervisor created
                                Supervisor supervisor = new Supervisor();
                                supervisor.setCompany(user.getCompany());
                                supervisor.setEmail(supervisorUserId.getText().toString());
                                supervisor.setName(supervisorName.getText().toString());
                                supervisor.setPassword(supervisorPwd.getText().toString());
                                supervisor.setSuperUser(user.getSuperUser());
                                supervisor.setSuperAdmin(user.getSuperAdmin());
                                supervisor.setUser(encryptedMailLoggedinUser);

                                //add supervisor to friend
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("friends");
                                ref.child(encryptedEmail).setValue(supervisor);

                                // adding supervisor to company node
                                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("companies");
                                ref1.child(user.getCompany())
                                        .child("superAdmin")
                                        .child(user.getSuperAdmin())
                                        .child("superUser")
                                        .child(user.getSuperUser())
                                        .child("user")
                                        .child(encryptedMailLoggedinUser)
                                        .child("supervisor")
                                        .child(encryptedEmail)
                                        .child("selfData")
                                        .setValue(supervisor);
                                pd.dismiss();
                                AddUserActivity.this.finish();
                                Toast.makeText(AddUserActivity.this,"Supervisor added successfully",Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            pd.dismiss();
                        }
                    });
                } else {
                    encryptedEmail = Helper.stringToBase64(etEmail.getText().toString());

                    final ProgressDialog pd = CustomProgressDialog.ctor(AddUserActivity.this);
                    pd.show();
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("friends").child(encryptedEmail);
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {

                                //new user created
                                User newUser = new User();
                                newUser.setName(etName.getText().toString());
                                newUser.setCompany(user.getCompany());
                                newUser.setEmail(etEmail.getText().toString());

                                if (user.getRole().equalsIgnoreCase("superAdmin")) {
                                    newUser.setSuperAdmin(encryptedMailLoggedinUser);
                                    newUser.setRole("superUser");
                                } else if (user.getRole().equalsIgnoreCase("superUser")) {
                                    newUser.setSuperAdmin(user.getSuperAdmin());
                                    newUser.setRole("user");
                                    newUser.setSuperUser(encryptedMailLoggedinUser);
                                }

                                //new user added to friends
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("friends");
                                ref.child(encryptedEmail).setValue(newUser);


                                //Adding user details inside company node
                                if (user.getRole().equalsIgnoreCase("superAdmin")) {
                                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("companies");
                                    ref1.child(user.getCompany())
                                            .child("superAdmin")
                                            .child(encryptedMailLoggedinUser)
                                            .child("superUser")
                                            .child(encryptedEmail)
                                            .child("selfData")
                                            .setValue(newUser);
                                    Toast.makeText(AddUserActivity.this,"Superuser added successfully",Toast.LENGTH_SHORT);
                                }

                                if (user.getRole().equalsIgnoreCase("superUser")) {
                                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("companies");
                                    ref1.child(user.getCompany())
                                            .child("superAdmin")
                                            .child(user.getSuperAdmin())
                                            .child("superUser")
                                            .child(encryptedMailLoggedinUser)
                                            .child("user")
                                            .child(encryptedEmail)
                                            .child("selfData")
                                            .setValue(newUser);
                                    Toast.makeText(AddUserActivity.this,"User added successfully",Toast.LENGTH_SHORT);
                                }
                                AddUserActivity.this.finish();
                            }
                            pd.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            pd.dismiss();
                        }
                    });
                }
            }
        });
    }
}
