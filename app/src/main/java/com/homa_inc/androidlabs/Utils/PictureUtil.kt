package com.homa_inc.androidlabs.Utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Point
import android.net.Uri
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
        const val TEMP_FILE_NAME = "IMG_TEMP.jpg"
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
            options.inSampleSize = inSampleSize*2
            return BitmapFactory.decodeFile(path, options) as Bitmap
        }
        public fun getScaledBitmap(bitmap: Bitmap, size: Point): Bitmap {
            var inSampleSize = 1
            if(bitmap.height > size.y || bitmap.width > size.y){
                if(bitmap.height > size.y)
                    inSampleSize = bitmap.height / size.y
                else
                    inSampleSize = bitmap.width / size.x
            }
            if(inSampleSize == 0)
                inSampleSize = 1
            return Bitmap.createScaledBitmap(bitmap,
                bitmap.width/inSampleSize,
                bitmap.height/inSampleSize,
                false)
        }
        fun getScaledBitmap(path: String, activity: AppCompatActivity): Bitmap{
            val size = Point()
            activity.windowManager.defaultDisplay.getSize(size)
            return getScaledBitmap(path, size.x, size.y)
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
            if(getTempPhotoFile(context)?.exists() as Boolean)
                savePicture(BitmapFactory.decodeFile(getTempPhotoFile(context)?.path), userFile)
        }

        fun getTempPhotoFile(context: Context?): File?{
            val externalFilesDir = context
                ?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ?: return null
            return File(externalFilesDir, TEMP_FILE_NAME)
        }
        fun fromUriInBitmap(activity: AppCompatActivity, uri: Uri?) : Bitmap{
            val parcelFileDescriptor = activity.contentResolver?.openFileDescriptor(uri as Uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()
            return image
        }
    }
}