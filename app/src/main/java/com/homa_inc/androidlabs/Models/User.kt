package com.homa_inc.androidlabs.Models

import android.content.Context
import com.orm.SugarContext
import com.orm.SugarRecord

class User : SugarRecord {
    var name: String? = null
    var surname: String? = null
    var password: String? = null
    var phone: String? = null
    var email: String? = null
    constructor(){}
    constructor(context: Context,
                name: String?,
                surname: String?,
                password:String?,
                phone: String?,
                email : String?) {
        SugarContext.init(context)
        this.name = name
        this.surname = surname
        this.password = password
        this.phone = phone
        this.email = email
    }
}