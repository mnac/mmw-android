package com.mmw.activity.tripDetail

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.mmw.R
import com.mmw.activity.BaseActivity
import com.mmw.activity.stageCreation.StageCreationActivity
import com.mmw.data.repository.TripRepository
import com.mmw.data.source.local.Preferences
import com.mmw.databinding.ActivityTripDetailBinding
import com.mmw.model.Trip
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class TripDetailActivity : BaseActivity() {
    companion object {
        val TRIP_KEY = "trip_key"
        val TRIP_ID_KEY = "trip_id_key"
    }

    private lateinit var binding: ActivityTripDetailBinding
    private lateinit var stagesAdapter: StagesAdapter

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = Preferences.getUserId(applicationContext)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_trip_detail)
        binding.viewModel = TripDetailViewModel(application, userId, TripRepository.instance)
        binding.handlers = this

        stagesAdapter = StagesAdapter(this, ArrayList(0))
        binding.stagesRecyclerView.adapter = stagesAdapter
        binding.stagesRecyclerView.isNestedScrollingEnabled = false
        binding.stagesRecyclerView.layoutManager = LinearLayoutManager(this)

        if (intent.hasExtra(TRIP_ID_KEY)) {
            handlePush(intent)
        } else if (intent.hasExtra(TRIP_KEY)) {
            val trip = intent.getParcelableExtra<Trip>(TRIP_KEY)
            handleTrip(trip)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null && intent.hasExtra(TRIP_ID_KEY)) {
            handlePush(intent)
        }
    }

    private fun handlePush(intent: Intent) {
        val tripId = intent.getStringExtra(TRIP_ID_KEY)

        disposable.add(TripRepository.instance.getTrip(tripId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        {},
                        {},
                        { trip -> handleTrip(trip) }
                ))
    }


    private fun handleTrip(trip: Trip) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = trip.title
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))

        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        binding.viewModel!!.loadTrip(trip)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun onClickFAB(@Suppress("UNUSED_PARAMETER") view: View) {
        if (binding.viewModel?.isOwner()!!) {
            val intent = Intent(this, StageCreationActivity::class.java)
            intent.putExtra(StageCreationActivity.TRIP_INTENT_KEY, binding.viewModel!!.trip)
            startActivityForResult(intent, StageCreationActivity.STAGE_ADDING_RESULT_KEY)
        } else {
            binding.viewModel?.handleClickFollow()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == StageCreationActivity.STAGE_ADDING_RESULT_KEY
                && resultCode == Activity.RESULT_OK) {
            binding.viewModel!!.loadTrip()
        }
    }

    override fun onDestroy() {
        binding.viewModel
        super.onDestroy()
    }
}
