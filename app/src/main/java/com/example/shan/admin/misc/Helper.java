package com.example.shan.admin.misc;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * Created by pc on 9/1/2018.
 */

public class Helper {

    public static String stringToBase64(String str) {
        String base64 = "";
        try {
            byte[] data = str.getBytes("UTF-8");
            base64 = Base64.encodeToString(data, Base64.NO_WRAP);
            Log.e("Helper", base64);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        return new String(base64);
    }


    public static String base64ToString(String base64) {
        byte[] data;
        data = Base64.decode(base64, Base64.NO_WRAP);
        Log.e("Helper", "" + data);
        return new String(data);
    }
}
