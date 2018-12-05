package com.homa_inc.androidlabs.Utils

import android.provider.ContactsContract
import android.util.Base64
import android.util.Log
import com.homa_inc.androidlabs.BuildConfig
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

fun loadKey(): String{
    try {
        val properties = Properties()
        with(FileInputStream("signing.properties")) {
            properties.load(this)
            return properties.getProperty("keystore.password")
        }
    } catch (ex: IOException){
        Log.e("CryptoUtil", "Config file 'signing.property' not found!")
        return BuildConfig.APPLICATION_ID
    }
}

@Throws(Exception::class)
fun String.encrypt(): String {
    val strKey = loadKey()
    val strData: String
    try {
        val sKeySpec = SecretKeySpec(strKey.toByteArray(), "Blowfish")
        val cipher = Cipher.getInstance("Blowfish")
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec)
        val encrypted = cipher.doFinal(this.toByteArray())
        strData = Base64.encodeToString(encrypted, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        throw Exception(e)
    }
    return strData
}

@Throws(Exception::class)
fun String.decrypt(): String {
    val strKey = loadKey()
    val strData: String
    try {
        val base64TextToDecrypt = Base64.decode(this.toByteArray(), Base64.DEFAULT)
        val sKeySpec = SecretKeySpec(strKey.toByteArray(), "Blowfish")
        val cipher = Cipher.getInstance("Blowfish")
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec)
        val decrypted = cipher.doFinal(base64TextToDecrypt)
        strData = String(decrypted)
    } catch (e: Exception) {
        e.printStackTrace()
        throw Exception(e)
    }
    return strData
}