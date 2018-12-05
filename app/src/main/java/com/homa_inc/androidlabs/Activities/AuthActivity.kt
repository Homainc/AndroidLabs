package com.homa_inc.androidlabs.Activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.homa_inc.androidlabs.R
import com.homa_inc.androidlabs.Utils.UserUtil

class AuthActivity : AppCompatActivity() {

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        navController = Navigation.findNavController(this@AuthActivity, R.id.nav_auth_host_fragment)
        UserUtil.init(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.appbar_item_about -> {
                navController?.navigate(R.id.aboutFragment2)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}