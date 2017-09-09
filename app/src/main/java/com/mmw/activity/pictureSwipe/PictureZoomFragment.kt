package com.mmw.activity.pictureSwipe

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mmw.R
import com.mmw.databinding.FragmentPictureZoomBinding

class PictureZoomFragment : Fragment() {

    private var viewDataBinding: FragmentPictureZoomBinding? = null

    companion object {
        val PATH_KEY = "path_key"

        fun newInstance(path: String?): PictureZoomFragment {
            val fragment = PictureZoomFragment()
            val args = Bundle()
            args.putString(PATH_KEY, path)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (viewDataBinding == null) {
            viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_picture_zoom, container, false)
            viewDataBinding?.viewModel = PictureZoomViewModel(activity.application)
            viewDataBinding?.viewModel?.picturePath?.set(arguments.getString(PATH_KEY, null))
        }

        return viewDataBinding?.root
    }
}
