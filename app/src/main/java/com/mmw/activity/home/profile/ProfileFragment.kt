package com.mmw.activity.home.profile

import android.app.Activity
import android.arch.lifecycle.LifecycleFragment
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListPopupWindow
import com.mmw.R
import com.mmw.activity.home.TripActionListener
import com.mmw.activity.home.TripsAdapter
import com.mmw.activity.onboarding.OnBoardingActivity
import com.mmw.activity.tripDetail.TripDetailActivity
import com.mmw.activity.userUpdate.UpdateActivity
import com.mmw.data.repository.TripRepository
import com.mmw.data.source.local.Preferences
import com.mmw.databinding.FragmentProfileBinding
import com.mmw.model.Trip
import com.mmw.model.User


/**
 * Created by Mathias on 27/08/2017.
 *
 */
class ProfileFragment : LifecycleFragment(), TripActionListener, AdapterView.OnItemClickListener {

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        if (listPopupWindow != null) listPopupWindow!!.dismiss()
        if (position == 0) {
            goToEditProfile(viewDataBinding!!.viewModel!!.user)
        } else if (position == 1) {
            Preferences.removeOauthCredentials(activity)
            goToOnBoardingActivity()
        }
    }

    private var viewDataBinding: FragmentProfileBinding? = null
    private var tripsAdapter: TripsAdapter? = null

    private var listPopupWindow: ListPopupWindow? = null
    private var options = arrayOf("Modifier mon profil", "Me déconnecter")


    companion object {
        fun newInstance() = ProfileFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (viewDataBinding == null) {
            viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
            viewDataBinding?.viewModel = ProfileViewModel(activity.application, TripRepository.instance)
            viewDataBinding?.handler = this
        }

        val imageButton = viewDataBinding?.root?.findViewById<ImageButton>(R.id.settingsImageBtn)

        listPopupWindow = ListPopupWindow(activity)
        listPopupWindow!!.setAdapter(ArrayAdapter(activity, R.layout.settings_item, options))
        listPopupWindow!!.anchorView = imageButton
        listPopupWindow!!.width = 496
        listPopupWindow!!.height = 288
        listPopupWindow!!.isModal = true
        listPopupWindow!!.setOnItemClickListener(this)

        return viewDataBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (tripsAdapter == null) {
            tripsAdapter = TripsAdapter(ArrayList(0), this)
            viewDataBinding?.tripsRecyclerVw?.adapter = tripsAdapter
            viewDataBinding?.tripsRecyclerVw?.layoutManager = LinearLayoutManager(context)
            loadData()
        }
    }

    private fun loadData() {
        viewDataBinding?.viewModel!!.loadProfile(false)
    }

    fun onClickSettings(view: View) {
        if (listPopupWindow != null) {
            listPopupWindow!!.show()
        }
    }

    override fun onClickTrip(trip: Trip) {
        val intent = Intent(activity, TripDetailActivity::class.java)
        intent.putExtra(TripDetailActivity.TRIP_KEY, trip)
        startActivity(intent)
    }

    private fun goToOnBoardingActivity() {
        val intent = Intent(activity, OnBoardingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        activity.finish()
    }

    private fun goToEditProfile(user: User?) {
        if (user != null) {
            val intent = Intent(activity, UpdateActivity::class.java)
            intent.putExtra(UpdateActivity.USER_KEY, user)
            startActivityForResult(intent, UpdateActivity.UPDATE_PROFILE_KEY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UpdateActivity.UPDATE_PROFILE_KEY && resultCode == Activity.RESULT_OK) {
            loadData()
        }
    }
}