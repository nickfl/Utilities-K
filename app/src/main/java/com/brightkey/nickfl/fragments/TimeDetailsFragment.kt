package com.brightkey.nickfl.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brightkey.nickfl.activities.MainActivity
import com.brightkey.nickfl.adapters.TimeListAdapter
import com.brightkey.nickfl.entities.BaseUtility_
import com.brightkey.nickfl.helpers.Constants
import com.brightkey.nickfl.helpers.PeriodManager
import com.brightkey.nickfl.models.TimeListModel
import com.brightkey.nickfl.myutilities.R
import timber.log.Timber

class TimeDetailsFragment : BaseFragment() {

    //    private OnDashboardInteractionListener mListener;

    private var detailsType = Constants.HydroType

    fun setDetailsType(type: String) {
        detailsType = type
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_time_details, container, false)
        setupRecycler(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.w("[$line] onResume()")
        activity?.invalidateOptionsMenu()
        (activity as MainActivity).setCustomOptions(R.menu.fragment)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.w("[$line] onAttach()")
//        if (context instanceof OnDashboardInteractionListener) {
//            mListener = (OnDashboardInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    override fun onDetach() {
        super.onDetach()
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.w("[$line] onDetach()")
        //        mListener = null;
    }

    //region Helpers
    private fun setupRecycler(view: View) {
        val utils = utilityBox!!.query().equal(BaseUtility_.utilityType, detailsType).build().find()
                                        .filter{ PeriodManager.shared.isDateInPeriod(it.datePaid) }
        val models = TimeListModel.convertToTimeList(utils, detailsType)
        val rv = view.findViewById<View>(R.id.recyclerTimeDetails) as RecyclerView
        rv.setHasFixedSize(true)
        val llm = LinearLayoutManager(activity)
        rv.layoutManager = llm
        val adapter = TimeListAdapter(activity!!, models)
        rv.adapter = adapter
    }

    companion object {

        fun newInstance(): TimeDetailsFragment {
            val fragment = TimeDetailsFragment()
            fragment.mTag = FragmentScreen.TIMEDETAILS_FRAGMENT
            return fragment
        }
    }
    //endregion
}
