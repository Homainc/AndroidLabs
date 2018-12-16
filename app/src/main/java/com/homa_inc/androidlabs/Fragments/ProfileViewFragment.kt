package com.homa_inc.androidlabs.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.homa_inc.androidlabs.Activities.AuthActivity
import com.homa_inc.androidlabs.Activities.MainActivity
import com.homa_inc.androidlabs.R
import com.homa_inc.androidlabs.Utils.NewsCachingUtil
import com.homa_inc.androidlabs.Utils.PictureUtil
import com.homa_inc.androidlabs.Utils.UserUtil

class ProfileViewFragment : Fragment() {

    private var textName: AppCompatTextView? = null
    private var textSurname: AppCompatTextView? = null
    private var textPhone: AppCompatTextView? = null
    private var textEmail: AppCompatTextView? = null
    private var profileImageView: AppCompatImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_profile, container, false)
        val editButton = v.findViewById<AppCompatButton>(R.id.profile_edit_button)
        editButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.profileEditFragment))
        textName = v.findViewById(R.id.textViewName)
        textSurname = v.findViewById(R.id.textViewSurname)
        textPhone = v.findViewById(R.id.textViewPhoneNumber)
        textEmail = v.findViewById(R.id.textViewEmail)
        profileImageView = v.findViewById(R.id.profileImageView)
        val logOutButton = v.findViewById<AppCompatButton>(R.id.log_out_button)
        logOutButton.setOnClickListener { logOutClick() }
        UserUtil.instance.tempPhotoIsActual = false
        updateUI()
        return v
    }

    private fun logOutClick(){
        NewsCachingUtil.clearCache()
        UserUtil.instance.logOut()
        startActivity(Intent(activity, AuthActivity::class.java))
        activity?.finish()
    }

    private fun updateUI(){
        val user = UserUtil.instance.currentUser
        textName?.text = user?.name
        textSurname?.text = user?.surname
        textPhone?.text = user?.phone
        textEmail?.text = user?.email
        updatePhotoView()
    }
    private fun updatePhotoView(){
        val userPhotoFile = UserUtil.instance.currentPhotoFile
        if(userPhotoFile == null || !userPhotoFile.exists()) {
            profileImageView?.setImageResource(R.drawable.blank_profile)
            return
        }
        Log.i("checkTAG", userPhotoFile.path)
        //val bitmap = PictureUtil.getScaledBitmap(photoFile?.path as String, activity as AppCompatActivity)
        val bitmap = PictureUtil.getRoundedBitMap(userPhotoFile.path as String, activity as AppCompatActivity)
        profileImageView?.setImageDrawable(bitmap)
    }
}