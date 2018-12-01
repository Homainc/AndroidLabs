package com.homa_inc.androidlabs

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.FileProvider
import androidx.core.graphics.BitmapCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.homa_inc.androidlabs.Utils.PictureUtil
import com.homa_inc.androidlabs.Utils.UserUtil
import java.io.File

class ProfileEditFragment : Fragment() {
    private val requestPhotoFromCamera = 0
    private val requestPhotoFromGallery = 1
    private val phoneNumberRegex: Regex = Regex("^[+][0-9]{10,13}$")

    private var nameTextEdit: TextInputEditText? = null
    private var surnameTextEdit: TextInputEditText? = null
    private var phoneTextEdit: TextInputEditText? = null
    private var emailTextEdit: TextInputEditText? = null
    private var editImageView: AppCompatImageView? = null
    private var photoButton: AppCompatImageButton? = null

    private var photoFile: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        val saveProfileButton = v.findViewById<AppCompatButton>(R.id.saveProfileButton)
        saveProfileButton.setOnClickListener{saveProfileClick()}
        nameTextEdit = v.findViewById(R.id.nameTextEdit)
        surnameTextEdit = v.findViewById(R.id.surnameTextEdit)
        phoneTextEdit = v.findViewById(R.id.phoneTextEdit)
        emailTextEdit = v.findViewById(R.id.emailTextEdit)
        editImageView = v.findViewById(R.id.editImageView)
        photoButton = v.findViewById<AppCompatImageButton>(R.id.photoButton)
        photoFile = UserUtil.instance.getPhotoFile(context)
        setupChoosePhotoDialog()
        updateUI()
        return v
    }
    private fun setupChoosePhotoDialog(){
        val tempFile = PictureUtil.getTempPhotoFile(context)
        // Dialog set up
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

    private fun updateUI(){
        val user = UserUtil.instance.loadUser(context)
        nameTextEdit?.setText(user.name)
        surnameTextEdit?.setText(user.surname)
        phoneTextEdit?.setText(user.phone)
        emailTextEdit?.setText(user.email)
        updatePhotoView()
    }

    private fun saveProfileClick(){
        if(isInputCorrect()) {
            val user = UserUtil.instance.loadUser(context)
            user.name = nameTextEdit?.text.toString()
            user.surname = surnameTextEdit?.text.toString()
            user.email = emailTextEdit?.text.toString()
            user.phone = phoneTextEdit?.text.toString()
            user.save()
            if (photoFile?.path != UserUtil.instance.getPhotoFile(context)?.path)
                PictureUtil.saveUserPicture(context, UserUtil.instance.getPhotoFile(context))
            findNavController().navigate(R.id.profileViewFragment)
        }
    }

    private fun choosePhotoFromGalleryClick(tempFile: File?){
        if(tempFile == null) {
            photoButton?.isEnabled = false
            return
        }
        val photoPicker = Intent(Intent.ACTION_GET_CONTENT)
        photoPicker.type = "image/*"
        startActivityForResult(photoPicker, requestPhotoFromGallery)
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
           BuildConfig.APPLICATION_ID, tempFile as File)
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
        startActivityForResult(captureImage, requestPhotoFromCamera)
    }

    private fun isInputCorrect(): Boolean{
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
            requestPhotoFromCamera -> {
                photoFile = PictureUtil.getTempPhotoFile(context)
                updatePhotoView()
            }
            requestPhotoFromGallery -> {
                photoFile = PictureUtil.getTempPhotoFile(context)
                PictureUtil.savePicture(PictureUtil.fromUriInBitmap(activity as AppCompatActivity, data?.data), photoFile)
                updatePhotoView()
            }
        }
    }
    private fun updatePhotoView(){
        if(photoFile == null || photoFile?.exists() == false) {
            editImageView?.setImageDrawable(null)
            return
        }
        Log.i("checkTAG", photoFile?.path)
        //val bitmap = PictureUtil.getScaledBitmap(photoFile?.path as String, activity as AppCompatActivity)
        val bitmap = PictureUtil.getRoundedBitMap(photoFile?.path as String, activity as AppCompatActivity)
        editImageView?.setImageDrawable(bitmap)
    }
}