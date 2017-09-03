package com.mmw.activity.tripCreation

import android.app.Activity
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.mmw.R
import com.mmw.activity.BaseActivity
import com.mmw.activity.stageCreation.StageCreationActivity
import com.mmw.data.repository.TripRepository
import com.mmw.databinding.ActivityTripCreationBinding
import com.mmw.helper.view.setupSnackBar
import com.mmw.helper.view.setupSnackBarRes
import com.mmw.model.Trip

class TripCreationActivity : BaseActivity(), LifecycleRegistryOwner {

    private lateinit var binding: ActivityTripCreationBinding

    companion object {
        @JvmStatic val TRIP_CREATION_RESULT_KEY = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trip_creation)
        binding.viewModel = TripCreationViewModel(application, TripRepository.instance)

        binding.viewModel?.createdTrip?.observe(this, Observer {
            if (it != null) goToStageActivity(it)
        })

        val content = this.findViewById<View>(android.R.id.content)
        content.setupSnackBar(this, binding.viewModel?.snackBarMessage, Snackbar.LENGTH_LONG)
        content.setupSnackBarRes(this, binding.viewModel?.snackBarMessageRes, Snackbar.LENGTH_LONG)

    }

    private fun goToStageActivity(trip: Trip?) {
        if (trip != null) {
            val intent = Intent(this, StageCreationActivity::class.java)
            intent.putExtra(StageCreationActivity.TRIP_INTENT_KEY, trip)
            startActivityForResult(intent, StageCreationActivity.STAGE_CREATION_RESULT_KEY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == StageCreationActivity.STAGE_CREATION_RESULT_KEY
                && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
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
