package com.homa_inc.androidlabs.Utils

import android.content.Context
import com.homa_inc.androidlabs.Models.User
import com.orm.SugarContext
import com.orm.SugarRecord

class UserUtil private constructor() {
    private object Holder {val INSTANCE = UserUtil() }
    companion object {
        val instance: UserUtil by lazy { Holder.INSTANCE }
    }
    fun loadUser(context: Context?) : User{
        SugarContext.init(context as Context)
        var user = SugarRecord.first(User::class.java)
        if(user == null)
            user = initDefaultUser(context)
        return user
    }
    private fun initDefaultUser(context: Context?) : User{
        val user = User(context as Context,"Inited", "",
            "", "", "")
        user.save()
        return user
    }
}