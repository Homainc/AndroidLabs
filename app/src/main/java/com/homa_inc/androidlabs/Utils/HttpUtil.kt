package com.homa_inc.androidlabs.Utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class HttpUtil {
    companion object {
        fun dataHandler(urlString: String?): String?{
            var resultStream: String? = null
            try {
                val url = URL(urlString)
                val httpConnection = url.openConnection() as HttpURLConnection
                if(httpConnection.responseCode == HttpURLConnection.HTTP_OK){
                    val inputStream = BufferedInputStream(httpConnection.inputStream)
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val result = StringBuilder()
                    var line: String? = ""
                    while(line != null){
                        line = reader.readLine()
                        result.append(line)
                    }
                    resultStream = result.toString()
                    httpConnection.disconnect()
                }
            } catch (ex :Exception){
                return null
            }
            return resultStream
        }

        fun bitmapFromUrl(urlString: String): Bitmap? {
            var img: Bitmap? = null
            try{
                val input = URL(urlString).openStream()
                img = BitmapFactory.decodeStream(input)
            } catch (ex: Exception){
                ex.printStackTrace()
            }
            return img
        }

        @Suppress("DEPRECATION")
        fun hasConnection(context: Context): Boolean{
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if(networkInfo != null && networkInfo.isConnected)
                return true
            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            if(networkInfo != null && networkInfo.isConnected)
                return true
            networkInfo = connectivityManager.activeNetworkInfo
            if(networkInfo != null && networkInfo.isConnected)
                return true
            return false
        }
    }
}