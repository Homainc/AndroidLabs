package com.homa_inc.androidlabs.Models

import android.content.Context
import com.orm.SugarContext
import com.orm.SugarRecord
import com.orm.dsl.Ignore

class User : SugarRecord {
    var name: String? = null
    var surname: String? = null
    var password: String? = null
    var phone: String? = null
    var email: String? = null
    constructor(){}
    constructor(name: String?,
                surname: String?,
                password:String?,
                phone: String?,
                email : String?) {
        this.name = name
        this.surname = surname
        this.password = password
        this.phone = phone
        this.email = email
    }
    @Ignore
    val photoPath: String
            get() = "IMG_" +  this.id + ".jpg"
}