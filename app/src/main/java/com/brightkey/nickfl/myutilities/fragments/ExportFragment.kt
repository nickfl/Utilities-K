package com.brightkey.nickfl.myutilities.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.activities.MainActivity
import com.brightkey.nickfl.myutilities.helpers.PermissionHelper
import com.brightkey.nickfl.myutilities.helpers.RealmStorageRecords
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class ExportFragment : BaseFragment() {

    private var group: RadioGroup? = null
    private var selected = R.id.radioButtonDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_export, container, false)
        setup(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.w("[$line] onResume()")
        activity?.invalidateOptionsMenu()
        (activity as MainActivity).setCustomOptions(R.menu.fragment)
        cleanup()
    }

    // will be used in MainActivity
    fun exportRecord() {
        val activity = activity as Activity

        //check permission first
        if (!PermissionHelper.haveWritePermissions(activity)) {
            PermissionHelper.requestWritePermissions(activity)
            return
        }

        if (RealmStorageRecords.exportRecords()) {
            Toast.makeText(activity, "Export Success!", Toast.LENGTH_LONG).show()
            exitListener?.onFragmentExit()
        } else {
            Toast.makeText(getActivity(), "Export Failed...", Toast.LENGTH_LONG).show()
        }
    }

    //region start Helpers
    private fun cleanup() {
        group?.check(selected)
    }

    private fun setup(view: View) {
        val backup = view.findViewById<Button>(R.id.buttonBackup)
        backup?.setOnClickListener {
            when (selected) {
                R.id.radioButtonDevice -> exportRecord()
                else -> Toast.makeText(activity, "Not Available Yet", Toast.LENGTH_LONG).show()
            }
        }
        group = view.findViewById(R.id.radioGroup)
        group?.setOnCheckedChangeListener { _, i -> selected = i }
    }

    companion object {

        fun newInstance(): ExportFragment {
            val fragment = ExportFragment()
            fragment.mTag = FragmentScreen.EXPORT_FRAGMENT
            return fragment
        }
    }
    //endregion
}// Required empty public constructor
