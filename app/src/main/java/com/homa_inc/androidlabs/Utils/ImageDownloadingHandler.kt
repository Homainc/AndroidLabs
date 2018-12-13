package com.homa_inc.androidlabs.Utils

import android.os.Handler
import android.os.Message
import com.homa_inc.androidlabs.Interfaces.HandlerCallBack
import java.lang.ref.WeakReference

class ImageDownloadingHandler(callback: HandlerCallBack) : Handler() {

    private val callback: WeakReference<HandlerCallBack> = WeakReference(callback)

    override fun handleMessage(msg: Message) {
        val service = callback.get()
        service?.handleMessage(msg)
    }
}