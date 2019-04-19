package com.brightkey.nickfl.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.brightkey.nickfl.activities.MainActivity
import com.brightkey.nickfl.myutilities.R
import timber.log.Timber

class PeriodFragment : BaseFragment(), AdapterView.OnItemSelectedListener {

    private var spinner: Spinner? = null
    private val periodNames = arrayOf("All Years", "2019", "2018")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_period, container, false)
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

    private fun setup(view: View) {
        spinner = view.findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            spinner?.setOnItemSelectedListener(this)
            val aAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, periodNames)
            aAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner?.adapter = aAdapter
        }
    }

    // OnItemSelectedListener
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        (activity as MainActivity).currentPeriod = periodNames[position]
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        fun newInstance(): PeriodFragment {
            val fragment = PeriodFragment()
            fragment.mTag = FragmentScreen.PERIOD_FRAGMENT
            return fragment
        }
    }
}
