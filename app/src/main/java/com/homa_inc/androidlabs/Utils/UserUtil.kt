package com.homa_inc.androidlabs.Utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.util.Log
import com.homa_inc.androidlabs.Extensions.encrypt
import com.homa_inc.androidlabs.Models.User
import com.orm.SugarContext
import com.orm.SugarRecord
import java.io.File

class UserUtil private constructor() {
    private object Holder {
        val INSTANCE = UserUtil()
    }

    companion object {
        private const val APP_PREFERENCES = "app_settings"
        private const val APP_PREFERENCES_EMAIL = "Id"
        private const val APP_PREFERENCES_PASSWORD = "Password"
        private const val APP_PREFERENCES_RSS_LINK = "RSSLink"
        val instance: UserUtil by lazy { Holder.INSTANCE }
        var picturesDir: File? = null
        var settings: SharedPreferences? = null
        fun init(context: Context?) {
            SugarContext.init(context)
            settings = context?.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
            picturesDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        }
    }

    var currentId: Long? = null
    var tempPhotoIsActual = false

    val currentUser: User?
        get() {
            currentId ?: return null
            return SugarRecord.findById(User::class.java, currentId)
        }

    val currentPhotoFile: File?
        get() {
            var filename = currentUser?.photoPath
            if(tempPhotoIsActual)
               filename = PictureUtil.TEMP_FILE_NAME
            picturesDir ?: return null
            currentUser ?: return null
            return File(picturesDir, filename)
        }

    val isAuthenticated: Boolean
        get() {
            if (settings?.contains(APP_PREFERENCES_EMAIL) as Boolean &&
                settings?.contains(APP_PREFERENCES_PASSWORD) as Boolean
            ) {
                val email = settings?.getString(APP_PREFERENCES_EMAIL, "not_email")
                val password = settings?.getString(APP_PREFERENCES_PASSWORD, "")
                return login(email as String, password as String)
            }
            return false
        }

    fun login(email: String, password: String): Boolean {
        val users = SugarRecord.find(User::class.java, "email = ?", email)
        if (users.isEmpty())
            return false
        val user = users.first()
        if (user.password == password.encrypt(user)) {
            settings?.edit()?.apply {
                putString(APP_PREFERENCES_EMAIL, email)
                putString(APP_PREFERENCES_PASSWORD, password)
                currentId = users.first().id
                apply()
            }
            return true
        }
        return false
    }

    fun register(user: User): Boolean {
        val dbUsers = SugarRecord.find(User::class.java, "email = ?", user.email)
        if(!dbUsers.isEmpty())
            return false
        val rawPassword = user.password
        user.save()
        user.password = user.password?.encrypt(user)
        user.save()
        return login(user.email as String, rawPassword as String)
    }

    fun logOut() {
        settings?.edit()?.apply {
            remove(APP_PREFERENCES_PASSWORD)
            remove(APP_PREFERENCES_EMAIL)
            if(settings?.contains(APP_PREFERENCES_RSS_LINK) as Boolean)
                remove(APP_PREFERENCES_RSS_LINK)
            apply()
        }
        currentId = null
    }

    fun setRSSLink(rssLink: String){
        settings?.edit()?.apply{
            putString(APP_PREFERENCES_RSS_LINK, rssLink)
            apply()
        }
    }

    fun getRSSLink(): String? = settings?.getString(APP_PREFERENCES_RSS_LINK, null)
}