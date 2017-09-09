package com.mmw.activity.userUpdate

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.mmw.AppConstant
import com.mmw.R
import com.mmw.activity.BaseActivity
import com.mmw.data.repository.UserRepository
import com.mmw.databinding.ActivityUserUpdateBinding
import com.mmw.helper.Convert
import com.mmw.helper.picture.PictureManager
import com.mmw.helper.view.setupSnackBar
import com.mmw.model.User
import java.io.File
import java.lang.Exception

class UpdateActivity : BaseActivity(), TransferListener {

    companion object {
        val USER_KEY = "user_key"
        val UPDATE_PROFILE_KEY = 10
    }

    private var cameraUri: Uri? = null
    private var tempPictureFile: File? = null

    private var binding: ActivityUserUpdateBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (binding == null) {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_user_update)
            binding?.viewModel = UpdateViewModel(application, UserRepository.instance)
            binding?.handler = this

            binding?.viewModel?.isUpdated?.observe(this, Observer {
                if (it!!) {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            })

            val content = this.findViewById<View>(android.R.id.content)
            content.setupSnackBar(this, binding?.viewModel?.snackBarMessage, Snackbar.LENGTH_LONG)

            val user = intent.getParcelableExtra<User>(USER_KEY)
            binding?.viewModel!!.initUser(user)
        }
    }

    fun onClickPicture(@Suppress("UNUSED_PARAMETER") view: View) {
        cameraUri = PictureManager.requestPicture(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val content = this.findViewById<View>(android.R.id.content)
        cameraUri = PictureManager.onPermissionResult(this, content, requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        tempPictureFile = PictureManager.onActivityResult(this, requestCode, resultCode, cameraUri, data)
        if (tempPictureFile != null) PictureManager.uploadFile(this, tempPictureFile, AppConstant.S3_PROFILE_PICTURES_BUCKET, this)
    }

    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
        binding?.viewModel?.progress!!.set(Convert.safeLongToInt(bytesCurrent * 100 / bytesTotal))
    }

    override fun onStateChanged(id: Int, state: TransferState?) {
        when (state) {
            TransferState.IN_PROGRESS -> binding?.viewModel?.pictureLoading?.set(true)
            else -> {
                binding?.viewModel?.pictureLoading?.set(false)
            }
        }


        if (state == TransferState.COMPLETED && tempPictureFile != null) {
            binding?.viewModel?.setPicture(tempPictureFile!!.name)
            tempPictureFile!!.delete()
        }
    }

    override fun onError(id: Int, ex: Exception?) {
        Log.i("Picture transfert", ex.toString())
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        binding?.viewModel?.saveState()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        binding?.viewModel?.restoreState()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding?.viewModel?.dispose()
    }
}
