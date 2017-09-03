package com.mmw.activity.onboarding

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import com.mmw.R
import com.mmw.activity.BaseActivity
import com.mmw.activity.login.LoginActivity
import com.mmw.activity.register.RegisterActivity
import com.mmw.databinding.ActivityOnBoardingBinding

class OnBoardingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityOnBoardingBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding)
        binding.handlers = this
    }

    fun onClickRegister(view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    fun onClickLogin(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
