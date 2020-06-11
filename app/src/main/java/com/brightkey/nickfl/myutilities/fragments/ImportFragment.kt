package com.brightkey.nickfl.myutilities.fragments

import android.content.Context
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
import com.brightkey.nickfl.myutilities.helpers.Constants.REQUEST_READ_PERMISSIONS
import com.brightkey.nickfl.myutilities.helpers.PermissionHelper
import com.brightkey.nickfl.myutilities.helpers.RealmStorageRecords
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class ImportFragment : BaseFragment() {

    private var selected = R.id.radioButtonDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTag = FragmentScreen.IMPORT_FRAGMENT
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_import, container, false)
        setup(view)
        return view
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
        (activity as MainActivity).setCustomOptions(R.menu.fragment, getString(R.string.drawer_import))
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

            if (RealmStorageRecords.importRecords()) {
                Toast.makeText(it, "Import Success!", Toast.LENGTH_LONG).show()
                exitListener?.onFragmentExit()
            } else {
                Toast.makeText(it, "Import Failed...", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun importFromDefaults() {
        RealmStorageRecords.loadDefaultAssets(requireContext())
        Toast.makeText(activity, "Import Defaults Success!", Toast.LENGTH_LONG).show()
        exitListener?.onFragmentExit()
    }

    //region start Helpers
    private fun setup(view: View) {
        val group = view.findViewById<RadioGroup>(R.id.radioGroup)
        group.check(R.id.radioButtonDevice)
        val load = view.findViewById<Button>(R.id.buttonBackup)
        load.setOnClickListener {
            when (selected) {
                R.id.radioButtonDevice -> importRecordsFromDevice()
                R.id.radioButtonDefault -> importFromDefaults()
                else -> Toast.makeText(activity, "Not Available Yet", Toast.LENGTH_LONG).show()
            }
        }
        group.setOnCheckedChangeListener { _, i -> selected = i }
    }
    //endregion
}
