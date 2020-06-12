package com.brightkey.nickfl.myutilities.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.activities.MainActivity
import com.brightkey.nickfl.myutilities.adapters.ExitFragmentListener
import com.brightkey.nickfl.myutilities.helpers.PeriodManager
import com.brightkey.nickfl.myutilities.helpers.Periods
import timber.log.Timber

class PeriodFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var mTag: FragmentScreen
    private var spinner: Spinner? = null
    private var selectedPeriod = 0
    private val periodNames = arrayOf("Current Year", "2019", "2018")
    private var exitListener: ExitFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTag = FragmentScreen.PERIOD_FRAGMENT
        val oldPeriod = arguments?.getString("periodNow") ?: "Current Year"
        selectedPeriod = periodNames.indexOf(oldPeriod)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_period, container, false)
        setup(view)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ExitFragmentListener) {
            exitListener = context
        } else {
            throw RuntimeException(activity.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onResume() {
        super.onResume()
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.w("[$line] onResume()")
        (activity as MainActivity).setCustomOptions(R.menu.fragment)
    }

    private fun setup(view: View) {
        spinner = view.findViewById(R.id.spinner)
        spinner?.let {
            it.onItemSelectedListener = this
            val aAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, periodNames)
            aAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            it.adapter = aAdapter
            it.setSelection(selectedPeriod, false)
        }

        // Update button
        val updateBtn = view.findViewById<Button>(R.id.button_update)
        updateBtn?.setOnClickListener {
            (activity as MainActivity).changePeriod(periodNames[selectedPeriod])
            when (selectedPeriod) {
                0 -> {PeriodManager.shared.updatePeriodForToday()
                      PeriodManager.shared.setCurrentPeriod(Periods.Current)}
                1 -> {PeriodManager.shared.updatePeriodForYear(2019)
                      PeriodManager.shared.setCurrentPeriod(Periods.Year2019)}
                2 -> {PeriodManager.shared.updatePeriodForYear(2018)
                      PeriodManager.shared.setCurrentPeriod(Periods.Year2018)}
            }
            exitListener?.onFragmentExit()
        }
    }

    // OnItemSelectedListener
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        selectedPeriod = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
