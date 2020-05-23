package com.brightkey.nickfl.myutilities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.activities.MainActivity
import timber.log.Timber

class UtilityFragment : BaseFragment() {

    private var selected = R.id.radioButtonHydro

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        activity?.invalidateOptionsMenu()
        (activity as MainActivity).setCustomOptions(R.menu.fragment)
    }

    //region start Helpers
    private fun utilitySelected(selected: Int) {
        (activity as MainActivity).backToCharts(selected)
    }

    private fun setup(view: View) {
        val group = view.findViewById<RadioGroup>(R.id.radioGroupUtilities)
        group.check(R.id.radioButtonHydro)
        val select = view.findViewById<Button>(R.id.buttonSelect)
        select.setOnClickListener {
            utilitySelected(selected)
        }
        group.setOnCheckedChangeListener { _, i -> selected = i }
    }

    companion object {

        fun newInstance(): UtilityFragment {
            val fragment = UtilityFragment()
            fragment.mTag = FragmentScreen.UTILITY_FRAGMENT
            return fragment
        }
    }
    //endregion
}