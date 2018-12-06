package com.homa_inc.androidlabs.Utils

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import android.os.Message
import java.io.IOException


class ThumbnailDownloader<T>(private val mResponseHandler: Handler) : HandlerThread("ThumbnailDownloader"){

    companion object {
        private const val MESSAGE_DOWNLOAD = 0
    }

    private var mHasQuit : Boolean = false
    private var mRequestHandler: Handler? = null
    private var mRequestMap: ConcurrentMap<T, String> = ConcurrentHashMap()
    private var mThumbnailDownloadListener: ThumbnailDownloadListener<T>? = null

    interface ThumbnailDownloadListener<T>{
        fun onThumbnailDownloaded(target: T, thumbnail: Bitmap)
    }

    fun setThumbnailDownloadListener(listener: ThumbnailDownloadListener<T>){
        mThumbnailDownloadListener = listener
    }

    override fun quit(): Boolean {
        mHasQuit = true
        return super.quit()
    }

    fun queueThumbnail(target: T, url: String?){
        if(url == null){
            mRequestMap.remove(target)
        }
        else{
            @Suppress("ReplacePutWithAssignment")
            mRequestMap.put(target, url)
            mRequestHandler?.obtainMessage(MESSAGE_DOWNLOAD, target)
                ?.sendToTarget()
        }
    }

    override fun onLooperPrepared() {
        mRequestHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    handleRequest(target)
                }
            }
        }
    }

    private fun handleRequest(target: T) {
        try {
            val url = mRequestMap[target] ?: return
            val bitmap = HTTPUtil().GetBitmapFromUrl(url)
            mResponseHandler.post{
                if(mRequestMap.get(target) != url || mHasQuit)
                    return@post
                mRequestMap.remove(target)
                mThumbnailDownloadListener?.onThumbnailDownloaded(target, bitmap)
            }
        } catch (ioe: IOException) {
            Log.e("checkTAG", "Error downloading image", ioe)
        }
    }

    fun clearQueue(){
        mRequestHandler?.removeMessages(MESSAGE_DOWNLOAD)
        mRequestMap.clear()
    }
}