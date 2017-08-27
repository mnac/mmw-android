package com.mmw.activity.home

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import com.mmw.R
import com.mmw.activity.tripCreation.TripCreationActivity


class HomeActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                R.id.navigation_home -> {

                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_favorite -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {

                    return@OnNavigationItemSelectedListener true
                }
            }
            false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener({ goToTripCreationActivity() })

        val navigation = findViewById<BottomNavigationView>(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun goToTripCreationActivity() {
        startActivity(Intent(this, TripCreationActivity::class.java))
    }
}
