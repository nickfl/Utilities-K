package com.brightkey.nickfl.myutilities.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.activities.MainActivity
import com.brightkey.nickfl.myutilities.adapters.ExitFragmentListener
import com.brightkey.nickfl.myutilities.helpers.Constants
import timber.log.Timber

class HydroFragment : BaseEditFragment(Constants.HydroType), View.OnClickListener {

    internal val paidOnPeakTag = 111
    internal val paidOnMidTag = 222
    internal val paidOffPeakTag = 333

    private var usedOnPeak: TextView? = null
    private var usedOnMid: TextView? = null
    private var usedOffPeak: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTag = FragmentScreen.HYDRO_FRAGMENT
        super.getArguments(arguments)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_hydro, container, false)
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
        (activity as MainActivity).setCustomOptions(R.menu.fragment, getString(R.string.utility_hydro_details))
        startUp {
            usedOnPeak?.setText(R.string.hydro_zero_used)
            usedOnMid?.setText(R.string.hydro_zero_used)
            usedOffPeak?.setText(R.string.hydro_zero_used)
        }
    }

    //region start Helpers
    private fun setup(view: View) {
        val acc = view.findViewById<TextView>(R.id.textHydroAcc)
        acc.text = accountNumber()

        //add payment button
        addPayment = view.findViewById(R.id.buttonAddHydroPayment)
        addPayment.setOnClickListener(this)

        // the same for all Utilities - Main Statement data
        setupMainStatement(view, this)

        // Details:
        val onPeak = view.findViewById<View>(R.id.includeOnPeak)
        val nameOnPeak = onPeak.findViewById<TextView>(R.id.textAmountViewName)
        nameOnPeak.setText(R.string.hydro_on_peak_pay)
        paidAmount0 = onPeak.findViewById(R.id.textAmountViewAmount)
        paidAmount0.addTextChangedListener(AmountTextWatcher(paidAmount0))
        paidAmount0.id = paidOnPeakTag
        usedOnPeak = onPeak.findViewById(R.id.textAmountViewPrice)

        val onMid = view.findViewById<View>(R.id.includeOnMid)
        val nameOnMid = onMid.findViewById<TextView>(R.id.textAmountViewName)
        nameOnMid.setText(R.string.hydro_on_mid_pay)
        paidAmount1 = onMid.findViewById(R.id.textAmountViewAmount)
        paidAmount1?.addTextChangedListener(AmountTextWatcher(paidAmount1!!))
        paidAmount1?.id = paidOnMidTag
        usedOnMid = onMid.findViewById(R.id.textAmountViewPrice)

        val offPeak = view.findViewById<View>(R.id.includeOffPeak)
        val nameOffPeak = offPeak.findViewById<TextView>(R.id.textAmountViewName)
        nameOffPeak.setText(R.string.hydro_off_peak_pay)
        paidAmount2 = offPeak.findViewById(R.id.textAmountViewAmount)
        paidAmount2?.addTextChangedListener(AmountTextWatcher(paidAmount2!!))
        paidAmount2?.id = paidOffPeakTag
        usedOffPeak = offPeak.findViewById(R.id.textAmountViewPrice)
    }

    override fun onClick(v: View) {
        if (v === addPayment) {
            saveFullBill()
            exitListener?.onFragmentExit()
            return
        }
        currentDateView = if (v === addStatementDay) billDate else dueDate
        DatePickerFragment(requireActivity()).showDatePicker()
    }

    private inner class AmountTextWatcher constructor(private val view: View) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            val value = editable.toString()
            val paid = if (value.isEmpty()) 0.0 else java.lang.Double.parseDouble(value)
            when (view.id) {
                paidOnPeakTag -> {
                    usedOnPeak?.text = String.format("(kWh) %.3f",  unit0(paid))
                }
                paidOnMidTag -> {
                    usedOnMid?.text = String.format("(kWh) %.3f",  unit1(paid))
                }
                paidOffPeakTag -> {
                    usedOffPeak?.text = String.format("(kWh) %.3f",  unit2(paid))
                }
            }
        }
    }
    //endregion

} // Required empty public constructor
