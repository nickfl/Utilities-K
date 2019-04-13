package com.brightkey.nickfl.fragments

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.brightkey.nickfl.adapters.DashboardAdapter
import com.brightkey.nickfl.models.DashboardModel
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.R

class DashboardFragment : BaseFragment(), DashboardAdapter.AdapterDashboardInterface {

    private var mListener: OnDashboardInteractionListener? = null
    internal var adapter: DashboardAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        setupRecycler(view)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnDashboardInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    fun reloadView() {
        adapter?.cleanUtilities()
        adapter?.notifyDataSetChanged()
    }

    //region Helpers
    private fun setupRecycler(view: View) {
        val model = DashboardModel.convertToDash(MyUtilitiesApplication.config!!)
        val rv = view.findViewById<View>(R.id.recyclerDashboard) as RecyclerView
        rv.setHasFixedSize(true)
        val llm = LinearLayoutManager(activity)
        rv.layoutManager = llm
        adapter = DashboardAdapter(activity!!, model, this)
        rv.adapter = adapter
    }
    //endregion

    // Delegate
    interface OnDashboardInteractionListener {
        fun onDashboardInteraction(itemId: String)
    }

    // AdapterDashboardInterface
    override fun utilityPressed(itemId: String) {
        mListener?.onDashboardInteraction(itemId)
    }

    companion object {

        fun newInstance(): DashboardFragment {
            val fragment = DashboardFragment()
            fragment.mTag = FragmentScreen.DASHBOARD_FRAGMENT
            return fragment
        }
    }
}
