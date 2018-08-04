package com.example.shan.admin;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {


    Button addSuperUserBtn;
    EditText email,nameEditTxt,supervisorUserId,supervisorPwd,supervisorName;
//    SharedPreferences userDetailPref;
    String encryptedEmail = "";
    String loggedUserRole;
    String loggedUserCompany;
    String encryptedMailLoggedinUser;

    TextView userDetailTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        email=(EditText)findViewById(R.id.editTxtSU);
        nameEditTxt=(EditText)findViewById(R.id.editTxtName);

        userDetailTxt=(TextView)findViewById(R.id.textView2);

        supervisorUserId=(EditText)findViewById(R.id.editTxtSupervisorUid);
        supervisorPwd=(EditText)findViewById(R.id.editTxtSupervisorPassword);
        supervisorName=findViewById(R.id.editTxtSupervisorName);

        SharedPreferences userDetailPref= getSharedPreferences("userDetail",0);
        Log.e("userDetail",userDetailPref.getAll().toString());
        userDetailTxt.setText(userDetailPref.getAll().toString());
        addSuperUserBtn =(Button)findViewById(R.id.submit);
        if(userDetailPref.getString("role","noRole").equalsIgnoreCase("user")){
            supervisorUserId.setVisibility(View.VISIBLE);
            supervisorPwd.setVisibility(View.VISIBLE);
            supervisorName.setVisibility(View.VISIBLE);
            email.setVisibility(View.GONE);
            nameEditTxt.setVisibility(View.GONE);
            addSuperUserBtn.setText("Add supervisor");

        }

        else{
            supervisorUserId.setVisibility(View.GONE);
            supervisorPwd.setVisibility(View.GONE);
            supervisorName.setVisibility(View.GONE);
            email.setVisibility(View.VISIBLE);
            nameEditTxt.setVisibility(View.VISIBLE);
        }

        if(userDetailPref.getString("role","noRole").equalsIgnoreCase("superUser")){
            addSuperUserBtn.setText("Add User");

        }

        else if(userDetailPref.getString("role","noRole").equalsIgnoreCase("superAdmin")){
            addSuperUserBtn.setText("Add Superuser");

        }






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



                if(loggedUserRole.equalsIgnoreCase("user")){
                    try {
                        byte[] data = email1.getBytes("UTF-8");
                        encryptedMailLoggedinUser  = Base64.encodeToString(data, Base64.NO_WRAP);
                        Log.e("Main",encryptedEmail);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    //Here encryptedEmail= encrypted supervisorUserId
                    encryptedEmail = "";
                    try {
                        byte[] data = supervisorUserId.getText().toString().getBytes("UTF-8");
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
                                    edit.putString("email",supervisorUserId.getText().toString());
                                edit.putString("name",supervisorName.getText().toString());
                                edit.putString("password",supervisorPwd.getText().toString());


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

                                if(loggedUserRole.equalsIgnoreCase("user")){
                                    String superUserParent= pref.getString("superUser","def");
                                    if(superUserParent!="def")
                                        edit.putString("superUser",superUserParent);

                                    edit.putString("user",encryptedMailLoggedinUser);
                                    String superAdminParent= pref.getString("superAdmin","def");
                                    if(superUserParent!="def")
                                        edit.putString("superAdmin",superAdminParent);
                                    edit.putString("role","user");
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

                                if(loggedUserRole.equalsIgnoreCase("user")){
                                    String superAdminParent= pref.getString("superAdmin","def");
                                    String superUserParent= pref.getString("superUser","def");
                                    DatabaseReference ref1=FirebaseDatabase.getInstance().getReference("companies");
                                    ref1.child(loggedUserCompany).child("superAdmin").child(superAdminParent).child("superUser")
                                            .child(superUserParent).child("user").child(encryptedMailLoggedinUser).child("supervisor")
                                            .child(encryptedEmail)
                                            .child("selfData").setValue(tempPref.getAll());
                                    tempPref.edit().clear().commit();

                                }





                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });






                }
                else{
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






            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
