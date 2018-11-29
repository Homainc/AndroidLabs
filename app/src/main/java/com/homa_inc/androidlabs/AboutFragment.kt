package com.homa_inc.androidlabs

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

class AboutFragment : Fragment() {

    private val _myPermissionsRequestPhoneState = 1
    private var textIMEI: AppCompatTextView? = null
    private var textVersion: AppCompatTextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_about, container, false)
        textIMEI = v.findViewById(R.id.textView_IMEI) as AppCompatTextView
        textVersion = v.findViewById(R.id.textView_version) as AppCompatTextView
        textVersion?.text = BuildConfig.VERSION_NAME
        showIMEI()
        return v
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
            grantResults: IntArray) {
        when (requestCode) {
            _myPermissionsRequestPhoneState -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    textIMEI?.text = getIMEI()
                } else {
                    textIMEI?.text = getString(R.string.not_access_text)
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("HardwareIds")
    private fun getIMEI() : String {
        val telephonyManager: TelephonyManager =
            activity?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(
                context as Context,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return resources.getString(R.string.not_access_text)
        }
        var stringIMEI: String? = telephonyManager.deviceId
        if (stringIMEI == null)
            stringIMEI = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
        return stringIMEI as String
    }

    private fun showIMEI(){
        if (ActivityCompat.checkSelfPermission(context as Context,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity as Activity,
                    Manifest.permission.READ_PHONE_STATE)) {
                showExploration()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), _myPermissionsRequestPhoneState)
            }
        } else {
            textIMEI?.text = getIMEI()
        }
    }

    private fun showExploration(){
        val builder = AlertDialog.Builder(context as Context)
        builder.setTitle(resources.getString(R.string.Exploration_title))
            .setMessage(resources.getString(R.string.IMEI_exploration_string))
            .setCancelable(false)
            .setNeutralButton(resources.getString(R.string.OK_string)){dialog,_ -> run {
                requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), _myPermissionsRequestPhoneState)
                dialog.dismiss()
            }}
        val exploration = builder.create()
        exploration.show()
    }
}