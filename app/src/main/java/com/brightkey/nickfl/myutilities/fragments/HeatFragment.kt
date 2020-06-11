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
import com.brightkey.nickfl.myutilities.helpers.Constants
import timber.log.Timber

class HeatFragment : BaseEditFragment(Constants.HeatType), View.OnClickListener {

    private var usedGas: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTag = FragmentScreen.HEAT_FRAGMENT
        super.getArguments(arguments)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_heat, container, false)
        setup(view)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseFragment.ExitFragmentListener) {
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
        cleanUp()
    }

    //region start Helpers
    private fun setup(view: View) {
        val acc = view.findViewById<View>(R.id.textHeatAcc) as TextView
        acc.text = accountNumber()

        addPayment = view.findViewById(R.id.buttonAddHeatPayment)
        addPayment.setOnClickListener(this)

        // the same for all Utilities - Main Statement data
        setupMainStatement(view, this)

        // Details:
        val onPeak = view.findViewById<View>(R.id.includeOnPeak)
        val nameOnPeak = onPeak.findViewById<TextView>(R.id.textAmountViewName)
        nameOnPeak.setText(R.string.heat_gas_pay)
        paidAmount0 = onPeak.findViewById(R.id.textAmountViewAmount)
        paidAmount0.addTextChangedListener(AmountTextWatcher())
        usedGas = onPeak.findViewById(R.id.textAmountViewPrice)
    }

    private fun cleanUp() {
        initMainStatement()
        if (!doEdit) {
            changeDateVisibility(true)
            usedGas?.setText(R.string.water_zero_used)
        } else {
            billForUtility()
        }
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

    private inner class AmountTextWatcher : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            usedGas?.text = String.format("(m3) %.3f", editableChanged(editable))
        }
    }
    //endregion
}
