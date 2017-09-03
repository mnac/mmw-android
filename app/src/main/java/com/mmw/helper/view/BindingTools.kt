package com.mmw.helper.view

import android.databinding.BindingAdapter
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.mmw.R
import com.mmw.activity.home.TripsAdapter
import com.mmw.activity.home.timeline.TimelineViewModel
import com.mmw.activity.tripDetail.StagesAdapter
import com.mmw.model.Stage
import com.mmw.model.Trip
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
fun loadImage(view: ImageView, url: String?) {
    if (url != null && !url.isEmpty()) {
        Picasso.with(view.context).load(url).fit().centerCrop().into(view)
    }
}

@BindingAdapter("bind:profileImageUrl")
fun loadProfileImage(view: ImageView, url: String?) {
    if (url != null && !url.isEmpty()) {
        Picasso.with(view.context).load(url).placeholder(R.drawable.ic_account_box_80dp).fit().centerCrop().into(view)
    } else {
        Picasso.with(view.context).load(R.drawable.ic_account_box_80dp).placeholder(R.drawable.ic_account_box_80dp).fit().centerCrop().into(view)
    }
}

@BindingAdapter("bind:pictureToZoom")
fun loadPictureToZoom(view: SubsamplingScaleImageView, url: String?) {

    val target = object : com.squareup.picasso.Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            view.setImage(ImageSource.bitmap(bitmap))
        }
        override fun onBitmapFailed(errorDrawable: Drawable?) {}
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
    }

    view.tag = target

    if (url != null && !url.isEmpty()) {
        view.orientation = SubsamplingScaleImageView.ORIENTATION_USE_EXIF
        view.post({
            Picasso.with(view.context).load(url).centerInside()
                    .resize(view.width, view.height).into(target)
        })
    }
}

@BindingAdapter("bind:onRefresh")
fun SwipeRefreshLayout.setSwipeRefreshLayoutOnRefreshListener(
        viewModel: TimelineViewModel) {
    setOnRefreshListener { viewModel.loadTasks(true) }
}


@BindingAdapter("bind:trips")
fun loadTrips(view: RecyclerView, trips: List<Trip>) {
    (view.adapter as TripsAdapter).setList(trips)
}

@BindingAdapter("bind:stages")
fun loadStages(view: RecyclerView, stages: List<Stage>) {
    (view.adapter as StagesAdapter).setList(stages)
}
