package com.homa_inc.androidlabs.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.homa_inc.androidlabs.Extensions.hideKeyboard
import com.homa_inc.androidlabs.R
import com.homa_inc.androidlabs.Utils.UserUtil
import es.dmoral.toasty.Toasty

class SettingsFragment : Fragment() {

    private var rssLinkTextEdit: TextInputEditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_settings, container, false)
        rssLinkTextEdit = v.findViewById<TextInputEditText>(R.id.rssLinkTextEdit)
        rssLinkTextEdit?.setText(UserUtil.instance.getRSSLink())
        val applyButton = v.findViewById<AppCompatButton>(R.id.applyButton)
        applyButton.setOnClickListener{applyClick()}
        return v
    }

    private fun applyClick(){
        hideKeyboard()
        UserUtil.instance.setRSSLink(rssLinkTextEdit?.text.toString())
        Toasty.success(context as Context, R.string.settings_saved, Toast.LENGTH_SHORT, true).show()
    }
}