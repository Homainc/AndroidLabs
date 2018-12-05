package com.homa_inc.androidlabs.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputEditText
import com.homa_inc.androidlabs.Activities.MainActivity
import com.homa_inc.androidlabs.R
import com.homa_inc.androidlabs.Utils.UserUtil

class LoginFragment : Fragment() {

    var emailTextEdit: TextInputEditText? = null
    var passwordTextEdit: TextInputEditText? = null
    var errorText: AppCompatTextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_login, container, false)
        v.findViewById<AppCompatButton>(R.id.signUpButton)
            .setOnClickListener(Navigation.createNavigateOnClickListener(R.id.signUpFragment))
        val logInButton = v.findViewById<AppCompatButton>(R.id.log_in_button)
        logInButton.setOnClickListener { logInClick() }
        emailTextEdit = v.findViewById(R.id.emailTextEdit)
        passwordTextEdit = v.findViewById(R.id.passwordTextEdit)
        errorText = v.findViewById(R.id.errorText)
        return v
    }

    private fun logInClick() {
        if (UserUtil.instance.login(emailTextEdit?.text.toString(), passwordTextEdit?.text.toString())) {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        } else {
            errorText?.setText(R.string.log_in_error)
        }
    }
}