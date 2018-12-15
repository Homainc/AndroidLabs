package com.homa_inc.androidlabs.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.TextViewCompat
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputEditText
import com.homa_inc.androidlabs.Activities.MainActivity
import com.homa_inc.androidlabs.Models.User
import com.homa_inc.androidlabs.R
import com.homa_inc.androidlabs.Utils.PictureUtil
import com.homa_inc.androidlabs.Utils.UserUtil

class SignUpFragment: ProfileFragment() {

    override val layoutId: Int = R.layout.fragment_sign_up

    private var passwordTextEdit: TextInputEditText? = null
    private var confirmPasswordTextEdit: TextInputEditText? = null
    private var errorText: TextViewCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        passwordTextEdit = v?.findViewById(R.id.passwordTextEdit)
        confirmPasswordTextEdit = v?.findViewById(R.id.passwordConfirmTextEdit)
        val signUpButton = v?.findViewById<AppCompatButton>(R.id.signUpButton)
        signUpButton?.setOnClickListener { signUpButtonClick() }
        return v
    }

    private fun signUpButtonClick(){
        if(isInputCorrect()) {
            val user = User()
            user.name = nameTextEdit?.text.toString()
            user.surname = surnameTextEdit?.text.toString()
            user.email = emailTextEdit?.text.toString()
            user.password = passwordTextEdit?.text.toString()
            user.phone = phoneTextEdit?.text.toString()
            if(UserUtil.instance.register(user)){
                if(UserUtil.instance.tempPhotoIsActual) {
                    UserUtil.instance.tempPhotoIsActual = false
                    PictureUtil.saveUserPicture(context, UserUtil.instance.currentPhotoFile)
                }
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }
        }
    }

    override fun isInputCorrect(): Boolean{
        if(!super.isInputCorrect())
            return false
        else if(passwordTextEdit?.text?.isEmpty() as Boolean ||
                confirmPasswordTextEdit?.text?.isEmpty() as Boolean) {
            passwordTextEdit?.error = resources.getString(R.string.password_required)
            confirmPasswordTextEdit?.error = resources.getString(R.string.password_required)
            return false
        }
        else if(passwordTextEdit?.text?.toString() != confirmPasswordTextEdit?.text?.toString()){
            confirmPasswordTextEdit?.error = resources.getString(R.string.passwords_must_match)
            return false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                Navigation.findNavController(activity as AppCompatActivity, R.id.nav_auth_host_fragment)
                    .popBackStack()
                (activity as AppCompatActivity).supportActionBar?.apply {
                    setDisplayHomeAsUpEnabled(false)
                    setHomeButtonEnabled(false)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}