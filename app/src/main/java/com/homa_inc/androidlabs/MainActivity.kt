package com.homa_inc.androidlabs

import android.Manifest
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager
import android.widget.TextView
import android.support.v7.app.AlertDialog


class MainActivity : AppCompatActivity() {

    private val _myPermissionsRequestPhoneState = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textViewVersion = findViewById<TextView>(R.id.textView_version)
        textViewVersion.text = BuildConfig.VERSION_NAME

        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    Manifest.permission.READ_PHONE_STATE)) {
                showExploration()
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                    _myPermissionsRequestPhoneState)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                    _myPermissionsRequestPhoneState)
            }
        } else {
            showIMEI()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            _myPermissionsRequestPhoneState -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showIMEI()
                } else {
                    val textViewIMEI = findViewById<TextView>(R.id.textView_IMEI)
                    textViewIMEI.text = getString(R.string.not_access_text)
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun showIMEI() {
        val textViewIMEI = findViewById<TextView>(R.id.textView_IMEI)
        val telephonyManager: TelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            textViewIMEI.text = getString(R.string.not_access_text)
        } else {
            textViewIMEI.text = telephonyManager.deviceId
        }
    }
    private fun showExploration(){
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Message")
            .setMessage(getString(R.string.IMEI_exploration_string))
            .setCancelable(false)
            .setNeutralButton("Okay"){dialog,_ -> dialog.cancel()}
        val exploration = builder.create()
        exploration.show()
    }
}
