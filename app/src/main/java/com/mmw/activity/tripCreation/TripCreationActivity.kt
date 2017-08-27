package com.mmw.activity.tripCreation

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.mmw.R
import com.mmw.activity.stageCreation.StageCreationActivity
import com.mmw.data.model.Trip
import com.mmw.data.repository.TripRepository
import com.mmw.databinding.ActivityTripCreationBinding
import com.mmw.helper.view.setupSnackBar
import com.mmw.helper.view.setupSnackBarRes

class TripCreationActivity : AppCompatActivity(), LifecycleRegistryOwner {

    // Temporary class until Architecture Components is final. Makes [AppCompatActivity] a
    // [LifecycleRegistryOwner].
    private val registry = LifecycleRegistry(this)
    override fun getLifecycle(): LifecycleRegistry = registry

    private lateinit var binding: ActivityTripCreationBinding

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
            startActivity(intent)
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
