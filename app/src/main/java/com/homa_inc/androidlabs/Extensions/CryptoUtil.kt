package com.homa_inc.androidlabs.Extensions

import android.util.Base64
import com.homa_inc.androidlabs.Models.User
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

fun saltX10(string: String): String {
    var result = string
    for(i in 0..10)
        result = "${result.last()}@3sd$result[ds[dSA!W${result.first()}"
    return result.hashCode().toString() + result
}

@Throws(Exception::class)
fun String.encrypt(key: User): String {
    val strKey = saltX10("${key.id} + ${key.email}")
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