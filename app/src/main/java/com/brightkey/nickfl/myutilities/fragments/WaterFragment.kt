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
import com.brightkey.nickfl.myutilities.databinding.FragmentWaterBinding
import com.brightkey.nickfl.myutilities.helpers.Constants
import com.brightkey.nickfl.myutilities.helpers.DateFormatters
import timber.log.Timber

class WaterFragment : BaseEditFragment(Constants.WaterType), View.OnClickListener {

    internal val paidWaterTag = 111
    internal val paidWasteTag = 222
    internal val paidStormTag = 333

    private lateinit var usedWater: TextView
    private lateinit var usedWaste: TextView
    private lateinit var usedStorm: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTag = FragmentScreen.WATER_FRAGMENT
        super.getArguments(arguments)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val bindings = FragmentWaterBinding.inflate(inflater, container, false)
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
        (activity as MainActivity).setCustomOptions(R.menu.fragment, getString(R.string.utility_water_details))
        startUp {
            usedWater.text = getString(R.string.water_zero_used)
            usedWaste.text = getString(R.string.water_zero_used)
            usedStorm.text = getString(R.string.water_zero_days)
        }
    }

    //region start Helpers
    private fun setup(view: View) {
        setupMainStatement(view, this)

        addPayment.setOnClickListener(this)

        // Details:
        paidAmount0.addTextChangedListener(AmountTextWatcher(paidAmount0))
        paidAmount2?.addTextChangedListener(AmountTextWatcher(paidAmount2!!))
    }

    private fun setupBindings(bindings: FragmentWaterBinding) {
        paymentTotal = bindings.includeStatementData.layoutPrice.textPriceViewAmount
        paidAmount0 = bindings.includeOnPeak.textAmountViewAmount
        paidAmount0.id = paidWaterTag
        bindings.includeOnPeak.textAmountViewName.setText(R.string.water_water_pay)
        usedWater = bindings.includeOnPeak.textAmountViewPrice
        paidAmount1 = bindings.includeOnMid.textAmountViewAmount
        paidAmount1?.isEnabled = false
        paidAmount1?.id = paidWasteTag
        bindings.includeOnMid.textAmountViewName.setText(R.string.water_waste_pay)
        usedWaste = bindings.includeOnMid.textAmountViewPrice
        paidAmount2 = bindings.includeOffPeak.textAmountViewAmount
        paidAmount2?.id = paidStormTag
        bindings.includeOffPeak.textAmountViewName.setText(R.string.water_storm_pay)
        usedStorm = bindings.includeOffPeak.textAmountViewPrice
        addPayment = bindings.buttonAddWaterPayment
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
                paidWaterTag -> {
                    val water = unit0(paid)
                    usedWater.text = String.format("(m3) %.3f", water)
                    val waste = water * 0.85
                    usedWaste.text = String.format("(m3) %.3f", waste)
                    paidAmount1?.setText(String.format("%.2f", unitWaste1(waste)))
                }
                paidWasteTag -> {
                    usedWaste.text = String.format("(m3) %.3f",  unit1(paid))
                }
                paidStormTag -> {
                    usedStorm.text = String.format("(days) %.3f",  unit2(paid))
                }
            }
        }
    }
    //endregion

}// Required empty public constructor
