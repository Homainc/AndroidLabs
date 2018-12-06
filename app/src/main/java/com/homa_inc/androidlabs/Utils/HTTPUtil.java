package com.homa_inc.androidlabs.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPUtil {
    private static String stream = "";

    public  HTTPUtil(){}

    public String GetHTTPDataHandler(String urlString){
        try{
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

            if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = r.readLine()) != null)
                    sb.append(line);
                stream = sb.toString();
                urlConnection.disconnect();
            }
        } catch (Exception ex){
            return  null;
        }
        return stream;
    }

    public Bitmap GetBitmapFromUrl(String url){
        Bitmap img = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            img = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("checkTAG", e.getMessage());
            e.printStackTrace();
        }
        return img;
    }
    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }
}
