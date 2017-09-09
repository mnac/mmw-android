package com.mmw.activity.pictureSwipe

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableField

/**
 * Created by Mathias on 03/09/2017.
 *
 */
class PictureZoomViewModel(context: Application) : AndroidViewModel(context) {

    val picturePath: ObservableField<String> = ObservableField()

}

