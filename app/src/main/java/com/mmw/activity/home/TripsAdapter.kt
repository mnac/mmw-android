package com.mmw.activity.home

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mmw.AppConstant
import com.mmw.R
import com.mmw.databinding.ItemTripBinding
import com.mmw.model.Stage
import com.mmw.model.Trip
import com.mmw.model.TripItemView


/**
 * Created by Mathias on 27/08/2017.
 *
 */
class TripsAdapter(private var trips: List<Trip>, private var tripActionListener: TripActionListener) : RecyclerView.Adapter<TripItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TripItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val binding = DataBindingUtil.inflate<ItemTripBinding>(layoutInflater, R.layout.item_trip, parent, false)
        return TripItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripItemViewHolder?, position: Int) {
        val tripItem = trips[position]

        var picturePath: String? = AppConstant.S3_TRIP_PICTURE_ROOT
        if (tripItem.pictureUrl != null) {
            picturePath += tripItem.pictureUrl
        } else if (tripItem.stages != null && tripItem.stages.size > 0){
            picturePath += tripItem.stages[0].pictureUrl
        } else {
            picturePath = ""
        }

        var count = 0
        var average = 0f
        if (tripItem.stages != null) {
            for (stage: Stage in tripItem.stages) {
                if (stage.rate != null) {
                    average += stage.rate
                    count++
                }
            }
        }

        average /= count

        val item = TripItemView(tripItem, picturePath, tripItem.title, average)

        holder?.bind(item, tripActionListener)
    }

    override fun getItemCount() = trips.size

    fun setList(trips: List<Trip>) {
        this.trips = trips
        notifyDataSetChanged()
    }
}