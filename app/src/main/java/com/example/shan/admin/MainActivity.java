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
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {


    private static final String TAG = "GoogleActivity";
    SignInButton signInButton;
    Button signOutButton;
    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    // [END declare_auth_listener]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signOutButton = (Button)findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();





        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]


        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]
                //updateUI(user);
                // [END_EXCLUDE]
            }
        };
        // [END auth_state_listener]



    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    // [END on_stop_remove_listener]



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        } else if (i == R.id.sign_out_button) {
            signOut();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String name=acct.getDisplayName();
            Log.d(TAG,name);
            Log.d(TAG,name);

            Toast.makeText(MainActivity.this,"google sign succeeded :"+result.toString(),Toast.LENGTH_SHORT).show();


            firebaseAuthWithGoogle(acct);
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            // updateUI(false);

            Toast.makeText(MainActivity.this,"google sign in failed :"+result.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getIdToken());


        final String userId=acct.getId();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                        Toast.makeText(MainActivity.this, "Firebase Authentication succeeded.",
                                Toast.LENGTH_SHORT).show();




                        String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Log.d("MainActivity", "current user :"+userId);
                        Log.d("MainActivity", "current user :"+userId);


                        newUserEntry(acct);
                    }
                });
    }

    private void newUserEntry(final GoogleSignInAccount acct){



        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

// Creating new user node, which returns the unique key value
// new user node would be /users/$userid/


// creating user object
        // User user = new User("Ravi Tamada", "ravi@androidhive.info");

// pushing user to 'users' node using the userId

        String userId=acct.getId();
        mDatabase.child(acct.getDisplayName()).child("Email").setValue(acct.getEmail());

        final String company;
//         data = new byte[0];
        String base64 = "";
        try {
            byte[] data = acct.getEmail().getBytes("UTF-8");
           base64  = Base64.encodeToString(data, Base64.NO_WRAP);
            Log.e("Main",base64);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final String a=new String(base64);
        Log.e("a",a);

        String b="friends";

        DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference(b);
        DatabaseReference mDatabase2 = mDatabase1.child(a);
        mDatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exist= dataSnapshot.exists();
                Log.e("Main",exist+"");


                if(!dataSnapshot.exists()){
                    signOut();
                }
                else {
                    Log.e("Main",dataSnapshot.child("role")+"");
                    Map<String, String> user= null;
                     SharedPreferences pref =null;
                    if(dataSnapshot.hasChildren()){
                        user = (Map<String, String>) dataSnapshot.getValue();
                        String role= (String) user.get("role");
                        Log.e("role",role);

                        pref = getSharedPreferences("userDetail",0);
                        SharedPreferences.Editor edit =pref.edit();
                        edit.putString("role",role);
                        edit.putString("email",user.get("email"));
//                        edit.putString("name",user.get("name"));
                        edit.putString("name",acct.getDisplayName());
                        edit.putString("company",user.get("company"));

                        if(role.equalsIgnoreCase("superUser")){
                            edit.putString("superAdmin",user.get("superAdmin"));
                        }

                        if(role.equalsIgnoreCase("user")){
                            edit.putString("superAdmin",user.get("superAdmin"));
                            edit.putString("superUser",user.get("superUser"));
                        }

                        edit.commit();


                        if(role.equalsIgnoreCase("superAdmin")){
                            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("companies").child(user.get("company")).child(a);
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.exists()){

                                    SharedPreferences pref=getSharedPreferences("userDetail",0);
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference();
                                    myRef.child("companies").child("company4").child("superAdmin")
                                            .child(a).child("selfData").setValue(pref.getAll());
                                }
                            }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }



                        startActivity(new Intent(MainActivity.this,HomeActivity.class));

                }



                }







               /* if("superAdmin".equalsIgnoreCase(user.get("role"))){
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();
                    myRef.child("companies").child("company4").child("superAdmin")
                            .child(a).child("nameKey").setValue(pref.getAll());

                }

               *//* else if(role=="superUser" || role=="user"){

                }*//*

                else{
                    signOut();
                }
*/


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e("error",databaseError.getMessage());

            }
        });

        SharedPreferences pre= getSharedPreferences("loggedUser",0);
        SharedPreferences.Editor editor=  pre.edit();
        editor.putString("email",acct.getEmail());
        editor.putString("name",acct.getDisplayName());
        editor.commit();




    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        boolean isLogoutSuccess=status.isSuccess();
                        if(isLogoutSuccess)
                            Toast.makeText(MainActivity.this,"Successfully logged out",Toast.LENGTH_LONG).show();
                    }
                });
    }
}
