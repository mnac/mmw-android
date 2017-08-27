package com.mmw.activity.home

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.mmw.data.model.Trip

/**
 * Created by Mathias on 27/08/2017.
 *
 */
class TripsAdapter(
        private var trips: List<Trip>,
        private val tripsViewModel: TripsViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

    }

    override fun getItemCount() = trips.size

    override fun getItemId(position: Int) = position.toLong()

    private fun setList(trips: List<Trip>) {
        this.trips = trips
        notifyDataSetChanged()
    }
}