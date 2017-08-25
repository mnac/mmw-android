package com.mmw.helper.view

/**
 * Created by Mathias on 24/08/2017.
 *
 * Snack bar Extension functions for View
 */

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.support.design.widget.Snackbar
import android.view.View

/**
 * Transforms static java function SnackBar.make() to an extension function on View.
 */
fun View.showSnackBar(snackBarText: String, timeLength: Int) {
    Snackbar.make(this, snackBarText, timeLength).show()
}

/**
 * Triggers a snack bar message when the value contained by snackBarTaskMessageLiveEvent is modified.
 */
fun View.setupSnackBarRes(lifecycleOwner: LifecycleOwner,
                          snackBarMessageLiveEvent: SingleLiveEvent<Int>?,
                          timeLength: Int) {
    snackBarMessageLiveEvent?.observe(
            lifecycleOwner,
            Observer {
                it?.let { showSnackBar(context.getString(it), timeLength) }
            })
}

/**
 * Triggers a snack bar message when the value contained by snackBarTaskMessageLiveEvent is modified.
 */
fun View.setupSnackBar(lifecycleOwner: LifecycleOwner,
                       snackBarMessageLiveEvent: SingleLiveEvent<String>?,
                       timeLength: Int) {
    snackBarMessageLiveEvent?.observe(
            lifecycleOwner,
            Observer {
                it?.let { showSnackBar(it, timeLength) }
            })
}