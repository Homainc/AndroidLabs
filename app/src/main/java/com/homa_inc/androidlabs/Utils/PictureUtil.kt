package com.homa_inc.androidlabs.Utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Point
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.BitmapCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class PictureUtil {
    companion object {
        private val tempFileName = "IMG_TEMP.jpg"
        private val debugTag = "AndroidLabs"
        private fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {
            var options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, options)
            val srcWidth = options.outWidth
            val srcHeight = options.outHeight
            var inSampleSize = 1
            if(srcHeight > destHeight || srcWidth > destWidth){
                if(srcWidth > srcHeight)
                    inSampleSize = srcHeight / destHeight
                else
                    inSampleSize = srcWidth / destWidth
            }
            options = BitmapFactory.Options()
            options.inSampleSize = inSampleSize
            return BitmapFactory.decodeFile(path, options) as Bitmap
        }
        fun getScaledBitmap(path: String, activity: AppCompatActivity): Bitmap{
            var size = Point()
            activity.windowManager.defaultDisplay.getSize(size)
            val matrix = Matrix().apply { postRotate(-90f) }
            val scaledBitmap = getScaledBitmap(path, size.x, size.y)
            return Bitmap.createBitmap(scaledBitmap, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix, true)
        }
        fun getRoundedBitMap(path: String,activity: AppCompatActivity): RoundedBitmapDrawable{
            val dr = RoundedBitmapDrawableFactory.create(activity.resources, getScaledBitmap(path, activity))
            dr.cornerRadius = 100f
            return dr
        }
        fun savePicture(bitmap: Bitmap, imgFile: File?){
            try {
                with(FileOutputStream(imgFile)){
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100,this)
                }
            } catch (ex: Exception){
                Log.d(debugTag, ex.message)
            }
        }
        fun saveUserPicture(context: Context?, userFile: File?){
            val bitmap = BitmapFactory.decodeFile(getTempPhotoFile(context)?.path)
            savePicture(bitmap, userFile)
        }
        fun getTempPhotoFile(context: Context?): File?{
            val externalFilesDir = context
                ?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ?: return null
            return File(externalFilesDir, tempFileName)
        }
    }
}