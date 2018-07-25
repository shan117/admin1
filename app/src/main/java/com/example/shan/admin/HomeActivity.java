package com.example.shan.admin;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {


    Button addSuperUserBtn;
    EditText email,nameEditTxt;
//    SharedPreferences userDetailPref;
    String encryptedEmail = "";
    String loggedUserRole;
    String loggedUserCompany;
    String encryptedMailLoggedinUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        email=(EditText)findViewById(R.id.editTxtSU);
        nameEditTxt=(EditText)findViewById(R.id.editTxtName);


        addSuperUserBtn =(Button)findViewById(R.id.submit);

        addSuperUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();*/

                SharedPreferences userDetailPref= getSharedPreferences("userDetail",0);
                String email1= userDetailPref.getString("email","no");
                String name= userDetailPref.getString("name","noName");
                loggedUserRole= userDetailPref.getString("role","def");
                loggedUserCompany=userDetailPref.getString("company","def");


                try {
                    byte[] data = email1.getBytes("UTF-8");
                    encryptedMailLoggedinUser  = Base64.encodeToString(data, Base64.NO_WRAP);
                    Log.e("Main",encryptedEmail);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                /*myRef.child("companies").child("company4").child("superAdmin")
                        .child(email1).child("nameKey").setValue(name);

                myRef.child("companies").child("company4").child("superAdmin")
                        .child(email1).child("superUser").child("name").setValue(nameEditTxt.toString());*/

                 encryptedEmail = "";
                try {
                    byte[] data = email.getText().toString().getBytes("UTF-8");
                    encryptedEmail  = Base64.encodeToString(data, Base64.NO_WRAP);
                    Log.e("Main",encryptedEmail);
                    Log.e("mail",email.getText().toString());

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("friends").child(encryptedEmail);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){

                            /*if(loggedUserRole=="superAdmin"){

                            }
                            else if(loggedUserRole=="superUser"){

                            }
                            else if(loggedUserRole=="user"){

                            }
*/
                            SharedPreferences pref= getSharedPreferences("userDetail",0);
                            SharedPreferences tempPref=getSharedPreferences("tempPref",0);
                            SharedPreferences.Editor edit=tempPref.edit();
                            edit.putString("company",pref.getString("company","def"));
                            edit.putString("email",email.getText().toString());


                            if(loggedUserRole.equalsIgnoreCase("superAdmin")){
                                edit.putString("superAdmin",encryptedMailLoggedinUser);
                                edit.putString("role","superUser");
                                edit.commit();
                            }


                            if(loggedUserRole.equalsIgnoreCase("superUser")){

                                String superUserParent= pref.getString("superAdmin","def");
                                if(superUserParent!="def")
                                edit.putString("superAdmin",superUserParent);
                                edit.putString("role","user");
                                edit.putString("superUser",encryptedMailLoggedinUser);
                                edit.commit();
                            }


                            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("friends");
                            ref.child(encryptedEmail).setValue(tempPref.getAll());

                            if(loggedUserRole.equalsIgnoreCase("superAdmin")){
                                DatabaseReference ref1=FirebaseDatabase.getInstance().getReference("companies");
                                ref1.child(loggedUserCompany).child("superAdmin").child(encryptedMailLoggedinUser)
                                        .child("superUser").child(encryptedEmail).child("selfData").setValue(tempPref.getAll());
                                tempPref.edit().clear().commit();
                            }

                            if(loggedUserRole.equalsIgnoreCase("superUser")){
                                String superUserParent= pref.getString("superAdmin","def");
                                DatabaseReference ref1=FirebaseDatabase.getInstance().getReference("companies");
                                ref1.child(loggedUserCompany).child("superAdmin").child(superUserParent)
                                        .child("superUser").child(encryptedMailLoggedinUser).child("user")
                                        .child(encryptedEmail).child("selfData").setValue(tempPref.getAll());
                                tempPref.edit().clear().commit();
                            }





                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}