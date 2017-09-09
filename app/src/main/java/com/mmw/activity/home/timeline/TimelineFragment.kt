package com.mmw.activity.home.timeline

import android.arch.lifecycle.LifecycleFragment
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mmw.R
import com.mmw.activity.home.TripActionListener
import com.mmw.activity.home.TripsAdapter
import com.mmw.activity.tripDetail.TripDetailActivity
import com.mmw.data.repository.TripRepository
import com.mmw.databinding.FragmentTimelineBinding
import com.mmw.model.Trip

/**
 * Created by Mathias on 27/08/2017.
 *
 */
class TimelineFragment : LifecycleFragment(), TripActionListener {

    private var viewDataBinding: FragmentTimelineBinding? = null
    private var tripsAdapter: TripsAdapter? = null

    companion object {
        fun newInstance() = TimelineFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        if (viewDataBinding == null) {
            viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline, container, false)
            viewDataBinding?.viewModel = TimelineViewModel(activity.application, TripRepository.instance)
        }

        return viewDataBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (tripsAdapter == null) {
            tripsAdapter = TripsAdapter(ArrayList(0), this)
            viewDataBinding?.tripsRecyclerVw?.adapter = tripsAdapter
            viewDataBinding?.tripsRecyclerVw?.layoutManager = LinearLayoutManager(context)
            loadTask()
        }
    }

    fun loadTask() {
        viewDataBinding?.viewModel!!.loadTasks(false)
    }

    override fun onClickTrip(trip: Trip) {
        val intent = Intent(activity, TripDetailActivity::class.java)
        intent.putExtra(TripDetailActivity.TRIP_KEY, trip)
        startActivity(intent)
    }
}