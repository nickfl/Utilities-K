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
import com.brightkey.nickfl.myutilities.helpers.Constants
import com.brightkey.nickfl.myutilities.helpers.DateFormatters
import timber.log.Timber

class HeatFragment : BaseEditFragment(Constants.HeatType), View.OnClickListener {

    private lateinit var usedGas: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTag = FragmentScreen.HEAT_FRAGMENT
        getArguments(arguments)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val bindings = FragmentHeatBinding.inflate(inflater, container, false)
        bindings.lifecycleOwner = this
        bindings.model = model
        setupBindings(bindings)
        val view = bindings.root
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
        (activity as MainActivity).setCustomOptions(R.menu.fragment, getString(R.string.utility_heat_details))
        startUp { usedGas.setText(R.string.water_zero_used) }
    }

    //region start Helpers
    private fun setup(view: View) {
        setupMainStatement(view, this)

        addPayment.setOnClickListener(this)

        // Details:
        paidAmount0.addTextChangedListener(AmountTextWatcher())
    }

    private fun setupBindings(bindings: FragmentHeatBinding) {
        paymentTotal = bindings.includeStatementData.layoutPrice.textPriceViewAmount
        paidAmount0 = bindings.includeOnPeak.textAmountViewAmount
        bindings.includeOnPeak.textAmountViewName.setText(R.string.heat_gas_pay)
        usedGas = bindings.includeOnPeak.textAmountViewPrice
        addPayment = bindings.buttonAddHeatPayment
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

    private inner class AmountTextWatcher : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            usedGas.text = String.format("(m3) %.3f", editableChanged(editable))
        }
    }
    //endregion
}
