package com.brightkey.nickfl.myutilities.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.activities.MainActivity
import com.brightkey.nickfl.myutilities.adapters.ExitFragmentListener
import com.brightkey.nickfl.myutilities.databinding.FragmentImportBinding
import com.brightkey.nickfl.myutilities.helpers.Constants.REQUEST_READ_PERMISSIONS
import com.brightkey.nickfl.myutilities.helpers.PermissionHelper
import com.brightkey.nickfl.myutilities.helpers.RealmStorageRecords
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class ImportFragment : Fragment() {

    private lateinit var mTag: FragmentScreen

    private var selected = R.id.radioButtonDevice
    private var exitListener: ExitFragmentListener? = null
    private lateinit var spinner: ProgressBar

    private var _binding: FragmentImportBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTag = FragmentScreen.IMPORT_FRAGMENT
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentImportBinding.inflate(inflater, container, false)
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
        (activity as MainActivity).setCustomOptions(R.menu.timeline, getString(R.string.drawer_import))
    }

    // will be used in MainActivity
    private fun importRecordsFromDevice() {
        val activity = activity

        //check permission first
        activity?.let {
            if (!PermissionHelper.haveReadPermissions(it)) {
                PermissionHelper.requestReadPermissions(it, REQUEST_READ_PERMISSIONS)
                return
            }

            showProgress()
            GlobalScope.launch {
                val res = RealmStorageRecords.importRecords()
                handler(res, "Import")
            }
        }
    }

    private fun importFromDefaults() {
        showProgress()
        GlobalScope.launch {
            RealmStorageRecords.loadDefaultAssets(requireContext())
            handler(true, "Import Defaults")
        }
    }

    private fun handler(result: Boolean, action: String) {
        activity?.runOnUiThread {
            if (result) {
                Toast.makeText(MyUtilitiesApplication.context, "$action Success!", Toast.LENGTH_LONG).show()
                exitListener?.onFragmentExit()
            } else {
                Toast.makeText(MyUtilitiesApplication.context, "$action Failed...", Toast.LENGTH_LONG).show()
            }
            hideProgress()
        }
    }

    //region start Helpers
    private fun setup() {
        val group = binding.radioGroup
        group.check(R.id.radioButtonDevice)
        binding.buttonBackup.setOnClickListener {
            when (selected) {
                R.id.radioButtonDevice -> importRecordsFromDevice()
                R.id.radioButtonDefault -> importFromDefaults()
                else -> Toast.makeText(activity, "Not Available Yet", Toast.LENGTH_LONG).show()
            }
        }
        group.setOnCheckedChangeListener { _, i -> selected = i }
        spinner = binding.progressBar
        spinner.visibility = View.INVISIBLE
    }

    private fun showProgress() {
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.i("[$line] Import Progress: Show")
        spinner.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.i("[$line] Import Progress: Hide")
        spinner.visibility = View.INVISIBLE
    }
    //endregion
}
