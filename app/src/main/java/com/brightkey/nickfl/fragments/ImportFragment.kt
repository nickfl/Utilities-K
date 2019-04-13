package com.brightkey.nickfl.fragments


import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast

import com.brightkey.nickfl.activities.MainActivity
import com.brightkey.nickfl.helpers.ImportExportRecords
import com.brightkey.nickfl.helpers.PermissionHelper
import com.brightkey.nickfl.myutilities.R

import timber.log.Timber

import com.brightkey.nickfl.helpers.Constants.REQUEST_READ_PERMISSIONS

/**
 * A simple [Fragment] subclass.
 */
class ImportFragment : BaseFragment() {

    private var selected = R.id.radioButtonDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_import, container, false)
        setup(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.w("[$line] onResume()")
        activity?.invalidateOptionsMenu()
        (activity as MainActivity).setCustomOptions(R.menu.fragment)
    }

    fun importRecord() {
        val activity = activity

        //check permission first
        if (!PermissionHelper.haveReadPermissions(activity!!)) {
            PermissionHelper.requestReadPermissions(activity!!, REQUEST_READ_PERMISSIONS)
            return
        }

        if (ImportExportRecords.importRecords(activity!!)) {
            Toast.makeText(activity, "Import Success!", Toast.LENGTH_LONG).show()
            exitListener?.onFragmentExit()
        } else {
            Toast.makeText(getActivity(), "Import Failed...", Toast.LENGTH_LONG).show()
        }
    }

    private fun importDefaults() {
        val activity = activity

        ImportExportRecords.loadDefaultAssets(activity!!)
        Toast.makeText(activity, "Import Defaults Success!", Toast.LENGTH_LONG).show()
        exitListener?.onFragmentExit()
    }

    //region start Helpers
    private fun setup(view: View) {
        val group = view.findViewById<RadioGroup>(R.id.radioGroup)
        group.check(R.id.radioButtonDevice)
        val load = view.findViewById<Button>(R.id.buttonBackup)
        load.setOnClickListener {
            if (selected == R.id.radioButtonDevice) {
                importRecord()
            } else if (selected == R.id.radioButtonDefault) {
                importDefaults()
            } else {
                Toast.makeText(activity, "Not Available Yet", Toast.LENGTH_LONG).show()
            }
        }
        group.setOnCheckedChangeListener { radioGroup, i -> selected = i }
    }

    companion object {

        fun newInstance(): ImportFragment {
            val fragment = ImportFragment()
            fragment.mTag = FragmentScreen.IMPORT_FRAGMENT
            return fragment
        }
    }
    //endregion
}// Required empty public constructor
