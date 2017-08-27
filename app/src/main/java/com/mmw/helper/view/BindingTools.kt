package com.mmw.helper.view

import android.databinding.BindingAdapter
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import android.widget.ListView
import com.mmw.activity.home.TripsViewModel
import com.mmw.data.model.Trip
import com.squareup.picasso.Picasso


/**
 * Created by Mathias on 24/08/2017.
 *
 */
@BindingAdapter("error")
fun setErrorMessage(view: TextInputLayout, errorMessage: String) {
    view.error = errorMessage
}

@BindingAdapter("focus")
fun onRequestFocus(view: TextInputEditText, requestFocus: Boolean) {
    if (requestFocus) view.requestFocus()
}

@BindingAdapter("bind:imageUrl")
fun loadImage(view: ImageView, url: String) {
    if (!url.isEmpty()) {
        Picasso.with(view.context).load(url).fit().centerCrop().into(view)
    }
}

@BindingAdapter("bind:onRefresh")
fun SwipeRefreshLayout.setSwipeRefreshLayoutOnRefreshListener(
        viewModel: TripsViewModel) {
    setOnRefreshListener { viewModel.loadTasks(true) }
}

@BindingAdapter("bind:tripItems")
fun setItems(view: RecyclerView, items: List<Trip>) {
    with(view.adapter as TripsAdapter) {
        replaceData(items)
    }
}