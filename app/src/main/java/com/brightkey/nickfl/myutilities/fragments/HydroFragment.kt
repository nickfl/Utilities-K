package com.brightkey.nickfl.myutilities.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.activities.MainActivity
import com.brightkey.nickfl.myutilities.application.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.entities.BaseUtility
import com.brightkey.nickfl.myutilities.helpers.Constants
import timber.log.Timber
import java.util.*

class HydroFragment : BaseFragment(), View.OnClickListener {

    private var usedOnPeak: TextView? = null
    private var usedOnMid: TextView? = null
    private var usedOffPeak: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_hydro, container, false)
        setup(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.w("[$line] onResume()")
        activity?.invalidateOptionsMenu()
        (activity as MainActivity).setCustomOptions(R.menu.fragment)
        cleanUp()
    }

    //region start Helpers
    private fun setup(view: View) {
        val acc = view.findViewById<TextView>(R.id.textHydroAcc)
        acc.text = entity?.accountNumber

        //add payment button
        addPayment = view.findViewById(R.id.buttonAddHydroPayment)
        addPayment?.setOnClickListener(this)

        // the same for all Utilities - Main Statement data
        super.setupMainStatement(view, this)

        // Details:
        val onPeak = view.findViewById<View>(R.id.includeOnPeak)
        val nameOnPeak = onPeak.findViewById<TextView>(R.id.textAmountViewName)
        nameOnPeak.setText(R.string.hydro_on_peak_pay)
        paidAmount0 = onPeak.findViewById(R.id.textAmountViewAmount)
        paidAmount0?.addTextChangedListener(AmountTextWatcher(paidAmount0!!))
        paidAmount0?.id = paidOnPeakTag
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

    private fun cleanUp() {
        super.initMainStatement()
        if (!doEdit!!) {
            changeDateVisibility(true)
            usedOnPeak?.setText(R.string.hydro_zero_used)
            usedOnMid?.setText(R.string.hydro_zero_used)
            usedOffPeak?.setText(R.string.hydro_zero_used)
        } else {
            editUtility = billForUtility(entity!!, editIndex)
        }
    }

    override fun onClick(v: View) {
        if (v === addPayment) {
            val check = ArrayList<EditText>()
            check.add(paidAmount0!!)
            check.add(paidAmount1!!)
            check.add(paidAmount2!!)
            if (!super.validateData(check)) {
                val line = Exception().stackTrace[0].lineNumber + 1
                Timber.e("[$line] validateData failed!")
                super.showError()
                return
            }
            val utility = if (doEdit!!) editUtility else BaseUtility()
            if (!doEdit!!) {
                super.saveMainStatement(utility!!, Constants.HydroType)
            }
            utility!!.amountDue = super.amountFrom(paymentTotal!!)
            utility.amountType0 = super.amountFrom(paidAmount0!!)
            utility.amountType1 = super.amountFrom(paidAmount1!!)
            utility.amountType2 = super.amountFrom(paidAmount2!!)
            //add or update payment
            utilityBox?.put(utility)
            exitListener?.onFragmentExit()
            return
        }
        currentDateView = if (v === addStatementDay) billDate else dueDate
        val newFragment = DatePickerFragment()
        newFragment.show(fragmentManager!!, "datePicker")
    }

    private inner class AmountTextWatcher constructor(private val view: View) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            var paid = if (editable.toString().isEmpty()) 0.0 else java.lang.Double.parseDouble(editable.toString())
            when (view.id) {
                paidOnPeakTag -> if (entity!!.unitPrice0 > 0.0) {
                    paid /= entity!!.unitPrice0
                    usedOnPeak?.setText(String.format("(kWh) %.3f", paid))
                }
                paidOnMidTag -> if (entity!!.unitPrice1 > 0.0) {
                    paid /= entity!!.unitPrice1
                    usedOnMid?.setText(String.format("(kWh) %.3f", paid))
                }
                paidOffPeakTag -> if (entity!!.unitPrice2 > 0.0) {
                    paid /= entity!!.unitPrice2
                    usedOffPeak?.setText(String.format("(kWh) %.3f", paid))
                }
            }
        }
    }

    companion object {

        internal val paidOnPeakTag = 111
        internal val paidOnMidTag = 222
        internal val paidOffPeakTag = 333

        fun newInstance(): HydroFragment {
            val fragment = HydroFragment()
            fragment.mTag = FragmentScreen.HYDRO_FRAGMENT
            fragment.entity = MyUtilitiesApplication.getConfigEntityForType(Constants.HydroType)
            return fragment
        }
    }
    //endregion

} // Required empty public constructor
