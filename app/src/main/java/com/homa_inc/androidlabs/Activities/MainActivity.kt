package com.homa_inc.androidlabs.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import androidx.transition.Visibility
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.homa_inc.androidlabs.Interfaces.ActionBarHomeButtonListener
import com.homa_inc.androidlabs.Interfaces.ActivityWithBackPressedListener
import com.homa_inc.androidlabs.Interfaces.OnBackPressedListener
import com.homa_inc.androidlabs.Interfaces.ProfileEditFragmentAttachedListener
import com.homa_inc.androidlabs.R
import com.homa_inc.androidlabs.Utils.UserUtil

class MainActivity : AppCompatActivity(), ActivityWithBackPressedListener,
    ProfileEditFragmentAttachedListener, ActionBarHomeButtonListener  {

    private var bottomNavigation: BottomNavigationView? = null
    private var navController: NavController? = null
    private var scrollWrapper: NestedScrollView? = null
    private var backPressedListener: OnBackPressedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scrollWrapper = findViewById(R.id.scrollWrapper)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        navController  = Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)
        bottomNavigation?.setupWithNavController(navController as NavController)
        UserUtil.init(this)
        if(!UserUtil.instance.isAuthenticated) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
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
                navController?.popBackStack()
                return true
            }
            R.id.homeAsUp -> {
                navController?.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onProfileEditFragmentHided() {
        val scale = resources.displayMetrics.density
        val bottomPaddingInPx =  Math.round(56 * scale + 0.5f)
        bottomNavigation?.visibility = View.VISIBLE
        scrollWrapper?.setPadding(
            scrollWrapper?.paddingLeft as Int,
            scrollWrapper?.paddingTop as Int,
            scrollWrapper?.paddingRight as Int,
            bottomPaddingInPx
        )
    }

    override fun onProfileEditFragmentShowed() {
        bottomNavigation?.visibility = View.GONE
        scrollWrapper?.setPadding(
            scrollWrapper?.paddingLeft as Int,
            scrollWrapper?.paddingTop as Int,
            scrollWrapper?.paddingRight as Int,
            0
        )
    }

    override fun hideHomeButton() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setHomeButtonEnabled(false)
        }
    }

    override fun showHomeButton() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
    }

    override fun setOnBackPressedListener(listener: OnBackPressedListener?) {
        backPressedListener = listener
    }

    override fun onBackPressed() {
        if(backPressedListener != null) {
            backPressedListener?.doBack()
            backPressedListener = null
        }
        else
            super.onBackPressed()
    }
}
