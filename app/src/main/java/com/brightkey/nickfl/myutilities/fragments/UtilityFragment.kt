package com.brightkey.nickfl.myutilities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.activities.MainActivity
import com.brightkey.nickfl.myutilities.helpers.Constants
import timber.log.Timber

class UtilityFragment : Fragment() {

    private var selected = R.id.radioButtonHydro
    private lateinit var oldType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        oldType = arguments?.getString("chartType") ?: Constants.HydroType
        when (oldType) {
            Constants.HydroType -> selected = R.id.radioButtonHydro
            Constants.HeatType -> selected = R.id.radioButtonGas
            Constants.PhoneType -> selected = R.id.radioButtonPhone
            Constants.WaterType -> selected = R.id.radioButtonWater
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_utility, container, false)
        setup(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.w("[$line] onResume()")
        (activity as MainActivity).setCustomOptions(R.menu.timeline)
    }

    //region start Helpers
    private fun utilitySelected(selected: Int) {
        (activity as MainActivity).backToCharts(selected)
    }

    private fun setup(view: View) {
        val group = view.findViewById<RadioGroup>(R.id.radioGroupUtilities)
        group.check(selected)
        val select = view.findViewById<Button>(R.id.buttonSelect)
        select.setOnClickListener {
            utilitySelected(selected)
        }
        group.setOnCheckedChangeListener { _, i -> selected = i }
    }
   //endregion
}