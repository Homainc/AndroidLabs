package com.homa_inc.androidlabs.Extensions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.homa_inc.androidlabs.Interfaces.NetListener

class NetReceiver(private val listener: NetListener) : BroadcastReceiver() {
    companion object {
        const val  CONNECTED = "isConnected"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val extras = intent?.extras
        val notConnected = extras?.getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY)
        if(!(notConnected as Boolean))
            listener.onConnectedNet()
    }
}