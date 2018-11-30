package com.homa_inc.androidlabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.homa_inc.androidlabs.Utils.UserUtil

class ProfileEditFragment : Fragment() {
    private var nameTextEdit: TextInputEditText? = null
    private var surnameTextEdit: TextInputEditText? = null
    private var phoneTextEdit: TextInputEditText? = null
    private var emailTextEdit: TextInputEditText? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        val saveProfileButton = v.findViewById<AppCompatButton>(R.id.saveProfileButton)
        saveProfileButton.setOnClickListener{saveProfileClick()}
        nameTextEdit = v.findViewById(R.id.nameTextEdit)
        surnameTextEdit = v.findViewById(R.id.surnameTextEdit)
        phoneTextEdit = v.findViewById(R.id.phoneTextEdit)
        emailTextEdit = v.findViewById(R.id.emailTextEdit)
        updateUI()
        return v
    }
    private fun updateUI(){
        val user = UserUtil.instance.loadUser(context)
        nameTextEdit?.setText(user.name)
        surnameTextEdit?.setText(user.surname)
        phoneTextEdit?.setText(user.phone)
        emailTextEdit?.setText(user.email)
    }
    private fun saveProfileClick(){
        val user = UserUtil.instance.loadUser(context)
        user.name = nameTextEdit?.text.toString()
        user.surname = surnameTextEdit?.text.toString()
        user.email = emailTextEdit?.text.toString()
        user.phone = phoneTextEdit?.text.toString()
        user.save()
        findNavController().navigate(R.id.profileViewFragment)
    }
}