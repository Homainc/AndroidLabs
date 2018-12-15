package com.homa_inc.androidlabs.Utils

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import android.os.Message
import com.homa_inc.androidlabs.Interfaces.HandlerCallBack
import java.io.IOException


class ThumbnailDownloader<T>(private val mResponseHandler: Handler) : HandlerThread("ThumbnailDownloader"),
    HandlerCallBack {

    companion object {
        const val MESSAGE_DOWNLOAD = 0
    }

    private var mHasQuit : Boolean = false
    private var mRequestHandler: Handler? = null
    private var mRequestMap: ConcurrentMap<T, String> = ConcurrentHashMap()
    private var mThumbnailDownloadListener: ThumbnailDownloadListener<T>? = null

    interface ThumbnailDownloadListener<T>{
        fun onThumbnailDownloaded(target: T, thumbnail: Bitmap?)
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
            mRequestMap[target] = url
            mRequestHandler?.obtainMessage(MESSAGE_DOWNLOAD, target)
                ?.sendToTarget()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun handleMessage(msg: Message) {
        if (msg.what == MESSAGE_DOWNLOAD) {
            val target = msg.obj as T
            handleRequest(target)
        }
    }

    override fun onLooperPrepared() {
        mRequestHandler = ImageDownloadingHandler(this)
    }

    private fun handleRequest(target: T) {
        try {
            val url = mRequestMap[target] ?: return
            val bitmap =  HttpUtil.bitmapFromUrl(url)
            mResponseHandler.post{
                if(mRequestMap[target] != url || mHasQuit)
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