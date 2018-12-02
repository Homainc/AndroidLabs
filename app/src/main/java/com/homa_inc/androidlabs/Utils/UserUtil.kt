package com.homa_inc.androidlabs.Utils

import android.content.Context
import android.os.Environment
import com.homa_inc.androidlabs.Models.User
import com.orm.SugarContext
import com.orm.SugarRecord
import java.io.File

class UserUtil private constructor() {
    private object Holder {val INSTANCE = UserUtil() }
    companion object {
        val instance: UserUtil by lazy { Holder.INSTANCE }
        var picturesDir: File? = null
        fun init(context: Context?){
            SugarContext.init(context)
            picturesDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        }
    }
    val currentUser: User
        get() = SugarRecord.first(User::class.java)?: initDefaultUser()

    private fun initDefaultUser() : User{
        val user = User("", "",
            "", "", "")
        user.save()
        return user
    }

    val currentPhotoFile: File?
        get(){
            picturesDir?: return null
            return File(picturesDir, currentUser.photoPath)
        }
}