package com.homa_inc.androidlabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

class ProfileViewFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_profile, container, false)
        val editButton = v.findViewById<AppCompatButton>(R.id.profile_edit_button)
        editButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.profileEditFragment))
        return v
    }
}