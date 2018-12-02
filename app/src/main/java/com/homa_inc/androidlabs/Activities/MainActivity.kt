package com.homa_inc.androidlabs.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.homa_inc.androidlabs.R
import com.homa_inc.androidlabs.Utils.UserUtil

class MainActivity : AppCompatActivity() {

    private var bottomNavigation: BottomNavigationView? = null
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        navController  = Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)
        bottomNavigation?.setupWithNavController(navController as NavController)
        UserUtil.init(this)
        startActivity(Intent(this, AuthActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.appbar_item_about -> {
                navController?.navigate(R.id.aboutFragment)
                return true
            }
            R.id.home -> {
                Log.i("checkTAG", "act_home")
                navController?.popBackStack()
                return true
            }
            R.id.homeAsUp -> {
                Log.i("checkTAG", "act_homeup")
                navController?.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
