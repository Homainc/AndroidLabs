package com.homa_inc.androidlabs.Fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.homa_inc.androidlabs.Adapter.BackPressedListener
import com.homa_inc.androidlabs.Extensions.hideKeyboard
import com.homa_inc.androidlabs.Interfaces.ActionBarHomeButtonListener
import com.homa_inc.androidlabs.Interfaces.ActivityWithBackPressedListener
import com.homa_inc.androidlabs.Interfaces.ProfileEditFragmentAttachedListener
import com.homa_inc.androidlabs.R
import com.homa_inc.androidlabs.Utils.PictureUtil
import com.homa_inc.androidlabs.Utils.UserUtil

class ProfileEditFragment : ProfileFragment() {

    private var profileEditListener: ProfileEditFragmentAttachedListener? = null
    private var actionBarListener: ActionBarHomeButtonListener? = null
    private var activityWithBackPressedListener: ActivityWithBackPressedListener? = null
    override val layoutId: Int = R.layout.fragment_edit_profile

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        profileEditListener = context as ProfileEditFragmentAttachedListener
        actionBarListener = context as ActionBarHomeButtonListener
        activityWithBackPressedListener = context as ActivityWithBackPressedListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activityWithBackPressedListener?.setOnBackPressedListener(
            BackPressedListener({onBackPressed()}))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)
        val saveProfileButton = v?.findViewById<AppCompatButton>(R.id.saveProfileButton)
        saveProfileButton?.setOnClickListener{saveProfileClick()}
        photoFile = UserUtil.instance.currentPhotoFile
        profileEditListener?.onProfileEditFragmentShowed()
        actionBarListener?.showHomeButton()
        updateUI()
        return v
    }

    private fun updateUI(){
        val user = UserUtil.instance.currentUser
        nameTextEdit?.setText(user?.name)
        surnameTextEdit?.setText(user?.surname)
        phoneTextEdit?.setText(user?.phone)
        emailTextEdit?.setText(user?.email)
        updatePhotoView()
    }

    private fun saveProfileClick(){
        if(isInputCorrect()) {
            UserUtil.instance.currentUser?.apply {
                name = nameTextEdit?.text.toString()
                surname = surnameTextEdit?.text.toString()
                email = emailTextEdit?.text.toString()
                phone = phoneTextEdit?.text.toString()
                save()
            }
            UserUtil.instance.tempPhotoIsActual = false
            if (photoFile?.path != UserUtil.instance.currentPhotoFile?.path) {
                PictureUtil.saveUserPicture(context, UserUtil.instance.currentPhotoFile)
            }
            profileEditListener?.onProfileEditFragmentHided()
            activityWithBackPressedListener?.setOnBackPressedListener(null)
            findNavController().navigate(R.id.profileViewFragment)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                actionBarListener?.hideHomeButton()
                hideKeyboard()
                showConfirmChangesDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showConfirmChangesDialog(){
        val builder = AlertDialog.Builder(context as Context)
        builder.apply {
            setTitle(R.string.text_warning)
            setMessage(R.string.unsaved_changes_message)
            setCancelable(false)
            setPositiveButton(R.string.text_save){ dialog, _ ->
                run {
                    saveProfileClick()
                    dialog.dismiss()
                    actionBarListener?.hideHomeButton()
                }
            }
            setNegativeButton(R.string.text_not_save) { dialog, _ ->
                run {
                    dialog.dismiss()
                    actionBarListener?.hideHomeButton()
                    profileEditListener?.onProfileEditFragmentHided()
                    findNavController().popBackStack()
                }
            }
        }
        val exploration = builder.create()
        exploration.show()
    }

    private fun onBackPressed(){
        showConfirmChangesDialog()
    }
}