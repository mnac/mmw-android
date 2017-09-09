package com.mmw.activity.home

import android.support.v7.widget.RecyclerView
import com.mmw.databinding.ItemTripBinding
import com.mmw.model.TripItemView


/**
 * Created by Mathias on 27/08/2017.
 *
 */
class TripItemViewHolder(var binding: ItemTripBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(tripItemView: TripItemView, actionListener: TripActionListener) {
        binding.actionListener = actionListener
        binding.trip = tripItemView
        binding.executePendingBindings()
    }
}