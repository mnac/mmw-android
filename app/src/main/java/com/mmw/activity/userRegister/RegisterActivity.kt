package com.mmw.activity.userRegister

import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.mmw.R
import com.mmw.activity.BaseActivity
import com.mmw.activity.home.HomeActivity
import com.mmw.data.repository.UserRepository
import com.mmw.databinding.ActivityRegisterBinding
import com.mmw.helper.view.setupSnackBar
import com.mmw.helper.view.setupSnackBarRes

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.viewModel = RegisterViewModel(application, UserRepository.instance)

        binding.viewModel?.isAuthenticated?.observe(this, Observer {
            if (it!!) goToHomeActivity()
        })

        val content = this.findViewById<View>(android.R.id.content)
        content.setupSnackBar(this, binding.viewModel?.snackBarMessage, Snackbar.LENGTH_LONG)
        content.setupSnackBarRes(this, binding.viewModel?.snackBarMessageRes, Snackbar.LENGTH_LONG)

        val password = this.findViewById<EditText>(R.id.passwordTxtEditTxt)
        password.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                binding.viewModel?.onClickRegister(v)
                true
            } else {
                false
            }
        }
    }

    private fun goToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        binding.viewModel?.saveState()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.viewModel?.restoreState()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewModel?.dispose()
    }
}
