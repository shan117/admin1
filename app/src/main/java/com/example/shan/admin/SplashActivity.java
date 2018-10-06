package com.example.shan.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shan.admin.misc.ConnectionDetector;


public class SplashActivity extends AppCompatActivity {
    Boolean isConnected = false;
    ConnectionDetector cd;

    RelativeLayout parent;

    String TAG = "SplashActvity";

    @Override
    protected void onCreate(Bundle anythinglikesplash) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(anythinglikesplash);
        setContentView(R.layout.activity_splash);

        parent = (RelativeLayout) findViewById(R.id.holder);
        reload();
    }


    private void reload() {
        cd = new ConnectionDetector(getApplicationContext());
        isConnected = cd.isConnectingToInternet(this);


        if (isConnected) {

            SharedPreferences prefs = getSharedPreferences("userDetail", 0);
            final boolean isSessionActive = prefs.getBoolean("isSessionActive", false);

            Thread timer = new Thread() {
                public void run() {
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.getStackTrace();
                    } finally {
                        if (isSessionActive) {
                            Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Intent i = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }

                    }
                }

            };
            timer.start();
        } else {
            final Snackbar snackbar = Snackbar
                    .make(parent, "Please check internet connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            reload();
                        }
                    });

            snackbar.setActionTextColor(Color.WHITE);

            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRed));
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }


    protected void onPause() {
        super.onPause();
        finish();
    }


}
