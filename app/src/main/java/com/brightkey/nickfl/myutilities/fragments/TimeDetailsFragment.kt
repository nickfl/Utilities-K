package com.brightkey.nickfl.myutilities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.activities.MainActivity
import com.brightkey.nickfl.myutilities.adapters.TimeListAdapter
import com.brightkey.nickfl.myutilities.databinding.FragmentTimeDetailsBinding
import com.brightkey.nickfl.myutilities.helpers.Constants.HydroType
import com.brightkey.nickfl.myutilities.helpers.RealmHelper
import com.brightkey.nickfl.myutilities.models.TimeListModel
import timber.log.Timber


class TimeDetailsFragment : Fragment() {

    private lateinit var detailsType: String
    private lateinit var title: String
    private lateinit var adapter: TimeListAdapter

    private var _binding: FragmentTimeDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailsType = arguments?.getString("billType") ?: HydroType
        title = MyUtilitiesApplication.getConfigEntityForType(detailsType)?.utilityType ?: "Utility"
        setHasOptionsMenu(true)
     }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTimeDetailsBinding.inflate(inflater, container, false)
        setupRecycler()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Fixing memory leak
        binding.recyclerTimeDetails.adapter = null
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.w("[$line] onResume()")
        (activity as MainActivity).setCustomOptions(R.menu.timeline, title)
        dataUpdated()
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
    private fun dataUpdated() {
        adapter.notifyDataSetChanged()
    }

//    private fun reloadView() {
//        adapter.cleanUtilities()
//        adapter.notifyDataSetChanged()
//    }

    private fun setupRecycler() {
        val utils = RealmHelper.utilitiesForType(detailsType)
        val models = TimeListModel.convertToTimeList(utils, detailsType)
        binding.recyclerTimeDetails.layoutManager = LinearLayoutManager(activity)
        binding.recyclerTimeDetails.setHasFixedSize(true)
        adapter = TimeListAdapter(requireActivity(), models)
        binding.recyclerTimeDetails.adapter = adapter
    }
    //endregion
}
