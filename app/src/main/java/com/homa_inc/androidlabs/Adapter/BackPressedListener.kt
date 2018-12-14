package com.homa_inc.androidlabs.Adapter

import com.homa_inc.androidlabs.Interfaces.OnBackPressedListener

class BackPressedListener(
    private val backFunc: () -> Unit) : OnBackPressedListener {

    override fun doBack() {
        backFunc.invoke()
    }
}