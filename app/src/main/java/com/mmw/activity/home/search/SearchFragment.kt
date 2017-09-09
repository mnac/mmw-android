package com.mmw.activity.home.search

import android.arch.lifecycle.LifecycleFragment
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import com.mmw.R
import com.mmw.activity.home.TripActionListener
import com.mmw.activity.home.TripsAdapter
import com.mmw.activity.tripDetail.TripDetailActivity
import com.mmw.data.repository.TripRepository
import com.mmw.databinding.FragmentSearchBinding
import com.mmw.model.Trip


/**
 * Created by Mathias on 5/09/2017.
 *
 */
class SearchFragment : LifecycleFragment(), TripActionListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private var mSearchView: SearchView? = null
    private var mCurFilter: String? = null

    private var viewDataBinding: FragmentSearchBinding? = null
    private var tripsAdapter: TripsAdapter? = null

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (viewDataBinding == null) {
            viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
            viewDataBinding?.viewModel = SearchViewModel(activity.application, TripRepository.instance)
        }

        return viewDataBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        if (tripsAdapter == null) {
            tripsAdapter = TripsAdapter(ArrayList(0), this)
            viewDataBinding?.tripsRecyclerVw?.adapter = tripsAdapter
            viewDataBinding?.tripsRecyclerVw?.layoutManager = LinearLayoutManager(context)

            getFavoriteTrips()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        val item = menu!!.add("Search")

        item.setIcon(R.drawable.ic_search_24dp)
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
        mSearchView = TripSearchView(activity)
        (mSearchView as TripSearchView).setOnQueryTextListener(this)
        (mSearchView as TripSearchView).setOnCloseListener(this)
        (mSearchView as TripSearchView).setIconifiedByDefault(false)
        (mSearchView as TripSearchView).background = ContextCompat.getDrawable(context, R.drawable.background_search)

        val editText = (mSearchView as TripSearchView).findViewById<EditText>(R.id.search_src_text)
        if (editText != null) {
            val colorWhite = ContextCompat.getColor(activity, android.R.color.white)
            editText.setTextColor(colorWhite)
            editText.setHintTextColor(colorWhite)
        }

        (mSearchView as TripSearchView).findViewById<ImageView>(R.id.search_close_btn)?.setImageResource(R.drawable.ic_close_24dp)
        (mSearchView as TripSearchView).findViewById<ImageView>(R.id.search_mag_icon)?.setImageResource(R.drawable.ic_search_24dp)

        item.actionView = mSearchView
    }

    class TripSearchView(context: Context) : SearchView(context) {
        override fun onActionViewCollapsed() {
            setQuery("", false)
            super.onActionViewCollapsed()
        }

        override fun onActionViewExpanded() {
            super.onActionViewExpanded()
            findViewById<EditText>(resources.getIdentifier("app:id/search_src_text", null, null))?.requestFocus()
        }
    }

    private fun getFavoriteTrips() {
        viewDataBinding?.viewModel!!.getFavoriteTrips()
    }

    private fun searchTrip() {
        if (mCurFilter.isNullOrEmpty()) getFavoriteTrips()
        else viewDataBinding?.viewModel!!.searchTrip(mCurFilter!!)
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {

        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val newFilter = if (!TextUtils.isEmpty(newText)) newText else null

        if (mCurFilter == null && newFilter == null) {
            return true
        }
        if (mCurFilter != null && mCurFilter.equals(newFilter)) {
            return true
        }

        mCurFilter = newFilter

        searchTrip()

        return true
    }

    override fun onClose(): Boolean {
        if (mSearchView != null) {
            if (!TextUtils.isEmpty(mSearchView!!.query)) {
                mSearchView!!.setQuery(null, true)
            }
        }
        return true
    }

    override fun onClickTrip(trip: Trip) {
        val intent = Intent(activity, TripDetailActivity::class.java)
        intent.putExtra(TripDetailActivity.TRIP_KEY, trip)
        startActivity(intent)
    }
}
