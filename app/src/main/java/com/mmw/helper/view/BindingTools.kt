package com.mmw.helper.view

import android.databinding.BindingAdapter
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout


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