package com.homa_inc.androidlabs.Interfaces

import android.view.View

interface NewsItemClickListener {
    fun onClick(view: View, position: Int, isLongClick: Boolean)
}
