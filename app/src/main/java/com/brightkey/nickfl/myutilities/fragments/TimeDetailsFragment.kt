package com.brightkey.nickfl.myutilities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.activities.MainActivity
import com.brightkey.nickfl.myutilities.adapters.TimeListAdapter
import com.brightkey.nickfl.myutilities.helpers.RealmHelper
import com.brightkey.nickfl.myutilities.models.TimeListModel
import timber.log.Timber


class TimeDetailsFragment : Fragment() {

    private lateinit var detailsType: String
    private lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailsType = arguments?.getString("billType") ?: "hydro_bill"
        title = MyUtilitiesApplication.getConfigEntityForType(detailsType)?.utilityType ?: "Utility"
        setHasOptionsMenu(true)
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
        (activity as MainActivity).setCustomOptions(R.menu.fragment, title)
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        val line = Exception().stackTrace[0].lineNumber + 1
//        Timber.w("[$line] onAttach()")
//    }

//    override fun onDetach() {
//        super.onDetach()
//        val line = Exception().stackTrace[0].lineNumber + 1
//        Timber.w("[$line] onDetach()")
//    }

    //region Helpers
    private fun setupRecycler(view: View) {
        val utils = RealmHelper.utilitiesForType(detailsType)
        val models = TimeListModel.convertToTimeList(utils, detailsType)
        val rv = view.findViewById<View>(R.id.recyclerTimeDetails) as RecyclerView
        rv.setHasFixedSize(true)
        val llm = LinearLayoutManager(activity)
        rv.layoutManager = llm
        val adapter = TimeListAdapter(requireActivity(), models)
        rv.adapter = adapter
    }
    //endregion
}
