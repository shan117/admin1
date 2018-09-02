package com.example.shan.admin.misc;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.example.shan.admin.R;


/**
 * Created by coderap on 4/9/2017.
 */

public class CustomSnackbar {

    SnackbarAction snackbarAction;

    public void setSnackbarAction(SnackbarAction snackbarAction) {
        this.snackbarAction = snackbarAction;
    }

    public static Bitmap Base64ToBitmap(String encodedImage)
    {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return  decodedByte;
    }

    public static void createSnackbarRed(String message, String option, View holder, Context context)
    {
        final Snackbar snackbar = Snackbar
                .make(holder, message, Snackbar.LENGTH_LONG)
                .setAction(option, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       
                    }
                });

        snackbar.setActionTextColor(Color.WHITE);

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        //textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbar.show();
    }



    public static void createSnackbarRed(String message, View holder, Context context)
    {
        final Snackbar snackbar = Snackbar
                .make(holder, message, Snackbar.LENGTH_LONG);

        snackbar.setActionTextColor(Color.WHITE);

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbar.show();
    }



    public  void createInternetSnackbar( View holder, Context context)
    {
        final Snackbar snackbar = Snackbar
                .make(holder, "Please check internet connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (snackbarAction!= null){
                            snackbarAction.onRetry();
                        }
                    }
                });

        snackbar.setActionTextColor(Color.WHITE);

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public interface SnackbarAction{
        public void onRetry();
    }
}
