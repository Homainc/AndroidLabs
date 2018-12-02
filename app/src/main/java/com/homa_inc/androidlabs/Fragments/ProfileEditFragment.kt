package com.homa_inc.androidlabs.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import com.homa_inc.androidlabs.R
import com.homa_inc.androidlabs.Utils.PictureUtil
import com.homa_inc.androidlabs.Utils.UserUtil
import java.io.File

class ProfileEditFragment : ProfileFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_edit_profile

    private var photoFile: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)
        val saveProfileButton = v?.findViewById<AppCompatButton>(R.id.saveProfileButton)
        saveProfileButton?.setOnClickListener{saveProfileClick()}
        photoFile = UserUtil.instance.getPhotoFile(context)
        updateUI()
        return v
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
}