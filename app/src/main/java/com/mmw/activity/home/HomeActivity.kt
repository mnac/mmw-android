package com.mmw.activity.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.util.Log
import com.mmw.R
import com.mmw.activity.BaseActivity
import com.mmw.activity.home.profile.ProfileFragment
import com.mmw.activity.home.timeline.TimelineFragment
import com.mmw.activity.tripCreation.TripCreationActivity
import com.mmw.data.repository.UserRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers


class HomeActivity : BaseActivity() {


    private val disposable = CompositeDisposable()

    private var timelineFragment: TimelineFragment? = null
    private var favoritesFragment: TimelineFragment? = null
    private var profileFragment: ProfileFragment? = null

    private val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                R.id.navigation_home -> {
                    selectFragment(0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_favorite -> {
                    selectFragment(1)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    selectFragment(2)
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

        timelineFragment = TimelineFragment.newInstance()
        favoritesFragment = TimelineFragment.newInstance()
        profileFragment = ProfileFragment.newInstance()
        selectFragment(0)


        val observable = UserRepository.instance.savePushToken(applicationContext)

        if (observable != null) {
            disposable.add(observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        { Log.e("Push token: ", "Push token not sent") },
                        {},
                        { Log.e("Push token: ", "Push token sent sucessfully") }
                ))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun selectFragment(position: Int) {
        when(position) {
            0 -> applyFragment(timelineFragment, "Feed")
            1 -> applyFragment(favoritesFragment, "Favoris")
            2 -> applyFragment(profileFragment, "Profile")
        }
    }

    private fun applyFragment(fragment: Fragment?, tag: String?) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment, tag).commit()
        if (supportActionBar != null) {
            supportActionBar!!.title = tag
        }
    }

    private fun goToTripCreationActivity() {
        startActivityForResult(Intent(this, TripCreationActivity::class.java), TripCreationActivity.TRIP_CREATION_RESULT_KEY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TripCreationActivity.TRIP_CREATION_RESULT_KEY
                && resultCode == Activity.RESULT_OK) {
            if (timelineFragment != null) {
                (timelineFragment as TimelineFragment).loadTask()
            }
        }
    }
}
