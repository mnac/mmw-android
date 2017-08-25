package com.mmw.activity.main

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mmw.activity.home.HomeActivity
import com.mmw.R
import com.mmw.activity.onboarding.OnBoardingActivity
import com.mmw.data.repository.UserRepository
import com.mmw.data.source.local.Preferences
import com.mmw.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity(), LifecycleRegistryOwner {

    // Temporary class until Architecture Components is final. Makes [AppCompatActivity] a
    // [LifecycleRegistryOwner].
    private val registry = LifecycleRegistry(this)
    override fun getLifecycle(): LifecycleRegistry = registry

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = MainViewModel(application, UserRepository.instance)

        if (Preferences.hasToken(applicationContext)) {
            goToHomeActivity()
        } else {
            goToOnBoardingActivity()
        }
    }

    private fun goToHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun goToOnBoardingActivity() {
        startActivity(Intent(this, OnBoardingActivity::class.java))
        finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.viewModel?.dispose()
    }
}