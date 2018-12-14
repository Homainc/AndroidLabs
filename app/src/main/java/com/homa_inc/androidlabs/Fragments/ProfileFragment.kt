package com.homa_inc.androidlabs.Fragments

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.homa_inc.androidlabs.BuildConfig
import com.homa_inc.androidlabs.R
import com.homa_inc.androidlabs.Utils.PictureUtil
import com.homa_inc.androidlabs.Utils.UserUtil
import java.io.File

abstract class ProfileFragment : Fragment() {

    abstract val layoutId: Int

    companion object {
        private const val REQUEST_CAMERA_PHOTO = 0
        private const val REQUEST_GALLERY_PHOTO = 1
        private val phoneNumberRegex: Regex = Regex("^[+][0-9]{10,13}$")
    }

    protected var nameTextEdit: TextInputEditText? = null
    protected var surnameTextEdit: TextInputEditText? = null
    protected var phoneTextEdit: TextInputEditText? = null
    protected var emailTextEdit: TextInputEditText? = null
    protected var editImageView: AppCompatImageView? = null
    protected var photoButton: AppCompatImageButton? = null

    protected var photoFile: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(layoutId, container, false)
        nameTextEdit = v.findViewById(R.id.nameTextEdit)
        surnameTextEdit = v.findViewById(R.id.surnameTextEdit)
        phoneTextEdit = v.findViewById(R.id.phoneTextEdit)
        emailTextEdit = v.findViewById(R.id.emailTextEdit)
        editImageView = v.findViewById(R.id.editImageView)
        photoButton = v.findViewById(R.id.photoButton)
        photoFile = UserUtil.instance.currentPhotoFile
        setupChoosePhotoDialog()
        return v
    }

    private fun setupChoosePhotoDialog(){
        val tempFile = PictureUtil.getTempPhotoFile(context)
        val builder = AlertDialog.Builder(context as Context)
        val choices = arrayOf(
            resources.getString(R.string.take_photo_from_camera),
            resources.getString(R.string.choose_photo_from_gallery))
        builder.apply {
            setCancelable(true)
            setItems(choices){_,which -> run {
                when(which){
                    0 -> takePhotoFromCameraClick(tempFile)
                    1 -> choosePhotoFromGalleryClick(tempFile)
                }
            }}
        }
        val dialog = builder.create()
        photoButton?.setOnClickListener{dialog.show()}
    }

    private fun choosePhotoFromGalleryClick(tempFile: File?){
        if(tempFile == null) {
            photoButton?.isEnabled = false
            return
        }
        val photoPicker = Intent(Intent.ACTION_GET_CONTENT)
        photoPicker.type = "image/*"
        startActivityForResult(photoPicker, REQUEST_GALLERY_PHOTO)
    }

    private fun takePhotoFromCameraClick(tempFile: File?){
        val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val canTakePhoto = tempFile != null &&
                captureImage.resolveActivity(activity?.packageManager as PackageManager) != null
        photoButton?.isEnabled = canTakePhoto
        if(!canTakePhoto)
            return
        captureImage.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val imgUri = FileProvider.getUriForFile(context as Context,
            BuildConfig.APPLICATION_ID, tempFile as File
        )
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
        startActivityForResult(captureImage, REQUEST_CAMERA_PHOTO)
    }

    protected open fun isInputCorrect(): Boolean{
        if(nameTextEdit?.text.isNullOrEmpty()) {
            nameTextEdit?.error = resources.getString(R.string.name_required)
            return false
        }
        else if(surnameTextEdit?.text.isNullOrEmpty()) {
            surnameTextEdit?.error = resources.getString(R.string.surname_required)
            return false
        }
        else if(emailTextEdit?.text.isNullOrEmpty()) {
            emailTextEdit?.error = resources.getString(R.string.email_required)
            return false
        }
        else if(phoneTextEdit?.text.isNullOrEmpty()){
            phoneTextEdit?.error = resources.getString(R.string.phone_number_required)
            return false
        }
        else if(!phoneNumberRegex.matches(phoneTextEdit?.text.toString())) {
            phoneTextEdit?.error = resources.getString(R.string.phone_number_incorrect)
            return false
        }
        else if(!(emailTextEdit?.text?.contains("@") as Boolean)) {
            emailTextEdit?.error = resources.getString(R.string.email_required)
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != AppCompatActivity.RESULT_OK)
            return
        when(requestCode){
            REQUEST_CAMERA_PHOTO -> {
                photoFile = PictureUtil.getTempPhotoFile(context)
                UserUtil.instance.tempPhotoIsActual = true
                updatePhotoView()
            }
            REQUEST_GALLERY_PHOTO -> {
                photoFile = PictureUtil.getTempPhotoFile(context)
                PictureUtil.savePicture(PictureUtil.fromUriInBitmap(activity as AppCompatActivity, data?.data), photoFile)
                UserUtil.instance.tempPhotoIsActual = true
                updatePhotoView()
            }
        }
    }

    protected fun updatePhotoView(){
        if(photoFile == null || photoFile?.exists() == false) {
            editImageView?.setImageDrawable(null)
            return
        }
        //val bitmap = PictureUtil.getScaledBitmap(photoFile?.path as String, activity as AppCompatActivity)
        val bitmap = PictureUtil.getRoundedBitMap(photoFile?.path as String, activity as AppCompatActivity)
        editImageView?.setImageDrawable(bitmap)
    }
}