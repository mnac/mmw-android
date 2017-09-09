package com.mmw.activity.pictureSwipe

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import com.mmw.R
import com.mmw.activity.BaseActivity



class PictureSwipeActivity : BaseActivity() {

    companion object {
        val PICTURE_PATHS_KEY = "picture_paths"
        val PICTURE_POSITION_KEY = "picture_position"
    }

    private var mPicturePager: ViewPager? = null

    var paths: ArrayList<String>? = null
    private var picturePosition = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_swiper)

        mPicturePager = findViewById(R.id.pager)

        paths = intent.getStringArrayListExtra(PICTURE_PATHS_KEY)
        picturePosition = intent.getIntExtra(PICTURE_POSITION_KEY, 0)
        if (paths != null) {
            val mPagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
            mPicturePager!!.setPageTransformer(true, DepthPageTransformer())
            mPicturePager!!.offscreenPageLimit = paths!!.size
            mPicturePager!!.adapter = mPagerAdapter
            mPicturePager!!.currentItem = picturePosition
        }
    }

    private inner class ScreenSlidePagerAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            val fragment = PictureZoomFragment()
            val bundle = Bundle()
            bundle.putString(PictureZoomFragment.PATH_KEY, paths!![position])
            fragment.arguments = bundle
            return fragment
        }

        override fun getCount(): Int {
            return paths!!.size
        }
    }

}
