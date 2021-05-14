package com.brightkey.nickfl.myutilities.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.activities.MainActivity
import com.brightkey.nickfl.myutilities.adapters.ExitFragmentListener
import com.brightkey.nickfl.myutilities.databinding.FragmentExportBinding
import com.brightkey.nickfl.myutilities.helpers.PermissionHelper
import com.brightkey.nickfl.myutilities.helpers.RealmStorageRecords
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ExportFragment : Fragment() {

    lateinit var mTag: FragmentScreen

    private var _binding: FragmentExportBinding? = null
    private val binding get() = _binding!!

    private var selected = R.id.radioButtonDevice
    private var exitListener: ExitFragmentListener? = null
    private lateinit var group: RadioGroup
    private lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTag = FragmentScreen.EXPORT_FRAGMENT
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentExportBinding.inflate(inflater, container, false)
        setup()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Fixing memory leak
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ExitFragmentListener) {
            exitListener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onResume() {
        super.onResume()
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.w("[$line] onResume()")
        (activity as MainActivity).setCustomOptions(
            R.menu.timeline,
            getString(R.string.drawer_export)
        )
        cleanup()
    }

    // will be used in MainActivity
    private fun exportRecord() {
        val activity = activity as Activity

        //check permission first
        if (!PermissionHelper.haveWritePermissions(activity)) {
            PermissionHelper.requestWritePermissions(activity)
            return
        }

        showProgress()
        CoroutineScope(Dispatchers.IO).launch {
            val res = RealmStorageRecords.exportRecords()
            handler(res, "Export")
        }
    }

    private fun handler(result: Boolean, action: String) {
        activity?.runOnUiThread {
            if (result) {
                Toast.makeText(
                    MyUtilitiesApplication.context,
                    "$action Success!",
                    Toast.LENGTH_LONG
                ).show()
                exitListener?.onFragmentExit()
            } else {
                Toast.makeText(
                    MyUtilitiesApplication.context,
                    "$action Failed...",
                    Toast.LENGTH_LONG
                ).show()
            }
            hideProgress()
        }
    }

    //region start Helpers
    private fun cleanup() {
        group.check(selected)
    }

    private fun setup() {
        binding.buttonBackup.setOnClickListener {
            when (selected) {
                R.id.radioButtonDevice -> exportRecord()
                else -> Toast.makeText(activity, "Not Available Yet", Toast.LENGTH_LONG).show()
            }
        }
        group = binding.radioGroup
        group.setOnCheckedChangeListener { _, i -> selected = i }
        spinner = binding.progressBar
        spinner.visibility = View.INVISIBLE
    }

    private fun showProgress() {
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.i("[$line] Export Progress: Show")
        spinner.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.i("[$line] Export Progress: Hide")
        spinner.visibility = View.INVISIBLE
    }
    //endregion

}// Required empty public constructor
