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
import com.brightkey.nickfl.myutilities.databinding.FragmentHeatBinding
import com.brightkey.nickfl.myutilities.databinding.FragmentHydroBinding
import com.brightkey.nickfl.myutilities.helpers.Constants
import com.brightkey.nickfl.myutilities.helpers.DateFormatters
import timber.log.Timber

class HydroFragment : BaseEditFragment(Constants.HydroType), View.OnClickListener {

    internal val paidOnPeakTag = 111
    internal val paidOnMidTag = 222
    internal val paidOffPeakTag = 333

    private lateinit var usedOnPeak: TextView
    private lateinit var usedOnMid: TextView
    private lateinit var usedOffPeak: TextView

    private var _binding: FragmentHydroBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTag = FragmentScreen.HYDRO_FRAGMENT
        super.getArguments(arguments)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHydroBinding.inflate(inflater, container, false)
        _binding?.lifecycleOwner = this
        _binding?.model = model
        setupBindings(binding)
        val view = binding.root
        setup(view)
        return view
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
        (activity as MainActivity).setCustomOptions(R.menu.fragment, getString(R.string.utility_hydro_details))
        startUp {
            usedOnPeak.text = getString(R.string.hydro_zero_used)
            usedOnMid.text = getString(R.string.hydro_zero_used)
            usedOffPeak.text = getString(R.string.hydro_zero_used)
        }
    }

    //region start Helpers
    private fun setup(view: View) {
        setupMainStatement(view, this)

        //add payment button
        addPayment.setOnClickListener(this)

        // Details:
        paidAmount0.let { it.addTextChangedListener(AmountTextWatcher(it)) }
        paidAmount1?.let { it.addTextChangedListener(AmountTextWatcher(it)) }
        paidAmount2?.let { it.addTextChangedListener(AmountTextWatcher(it)) }
    }

    private fun setupBindings(bindings: FragmentHydroBinding) {
        paymentTotal = binding.includeStatementData.layoutPrice.textPriceViewAmount
        paidAmount0 = bindings.includeOnPeak.textAmountViewAmount
        paidAmount0.id = paidOnPeakTag
        bindings.includeOnPeak.textAmountViewName.setText(R.string.hydro_on_peak_pay)
        usedOnPeak = bindings.includeOnPeak.textAmountViewPrice
        paidAmount1 = bindings.includeOnMid.textAmountViewAmount
        paidAmount1?.id = paidOnMidTag
        bindings.includeOnMid.textAmountViewName.setText(R.string.hydro_on_mid_pay)
        usedOnMid = bindings.includeOnMid.textAmountViewPrice
        paidAmount2 = bindings.includeOffPeak.textAmountViewAmount
        paidAmount2?.id = paidOffPeakTag
        bindings.includeOffPeak.textAmountViewName.setText(R.string.hydro_off_peak_pay)
        usedOffPeak = bindings.includeOffPeak.textAmountViewPrice
        addPayment = bindings.buttonAddHydroPayment
    }

    override fun onClick(v: View) {
        if (v === addPayment) {
            saveFullBill()
            exitListener?.onFragmentExit()
            return
        }
        currentDateView = if (v === addStatementDay) billDate else dueDate
        val date = DateFormatters.dateFromString(currentDateView?.text.toString())
        DatePickerFragment(requireActivity(), date).showDatePicker()
    }

    private inner class AmountTextWatcher constructor(private val view: View) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            val value = editable.toString()
            val paid = if (value.isEmpty()) 0.0 else java.lang.Double.parseDouble(value)
            when (view.id) {
                paidOnPeakTag -> {
                    usedOnPeak.text = String.format("(kWh) %.3f",  unit0(paid))
                }
                paidOnMidTag -> {
                    usedOnMid.text = String.format("(kWh) %.3f",  unit1(paid))
                }
                paidOffPeakTag -> {
                    usedOffPeak.text = String.format("(kWh) %.3f",  unit2(paid))
                }
            }
        }
    }
    //endregion

} // Required empty public constructor
