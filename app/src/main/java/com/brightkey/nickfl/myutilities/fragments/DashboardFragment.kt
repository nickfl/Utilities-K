package com.brightkey.nickfl.myutilities.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.activities.MainActivity
import com.brightkey.nickfl.myutilities.adapters.DashboardAdapter
import com.brightkey.nickfl.myutilities.databinding.FragmentDashboardBinding
import com.brightkey.nickfl.myutilities.models.DashboardModel
import timber.log.Timber

class DashboardFragment : Fragment(), DashboardAdapter.AdapterDashboardInterface {

    private var mListener: OnDashboardInteractionListener? = null
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        setupRecycler()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Fixing memory leak
        binding.recyclerDashboard.adapter = null
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDashboardInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(activity.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onResume() {
        super.onResume()
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.w("[$line] onResume()")
        (activity as MainActivity).setCustomOptions(R.menu.main, getString(R.string.title_myutility))
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    //region Helpers
    private fun setupRecycler() {
        val models = DashboardModel.convertToDash(MyUtilitiesApplication.config!!)
        binding.recyclerDashboard.layoutManager = LinearLayoutManager(activity)
        binding.recyclerDashboard.setHasFixedSize(true)
        binding.recyclerDashboard.adapter = DashboardAdapter(requireActivity(), models, this)
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
}
