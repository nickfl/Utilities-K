package com.brightkey.nickfl.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.brightkey.nickfl.activities.MainActivity
import com.brightkey.nickfl.entities.BaseUtility
import com.brightkey.nickfl.helpers.Constants
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.R
import timber.log.Timber
import java.util.*

class WaterFragment : BaseFragment(), View.OnClickListener {

    private var usedWater: TextView? = null
    private var usedWaste: TextView? = null
    private var usedStorm: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_water, container, false)
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
        val acc = view.findViewById<View>(R.id.textWaterAcc) as TextView
        acc.text = entity?.accountNumber

        addPayment = view.findViewById(R.id.buttonAddWaterPayment)
        addPayment?.setOnClickListener(this)

        // the same for all Utilities - Main Statement data
        super.setupMainStatement(view, this)

        // Details:
        val onPeak = view.findViewById<View>(R.id.includeOnPeak)
        val nameOnPeak = onPeak.findViewById<TextView>(R.id.textAmountViewName)
        nameOnPeak.setText(R.string.water_water_pay)
        paidAmount0 = onPeak.findViewById(R.id.textAmountViewAmount)
        paidAmount0?.addTextChangedListener(AmountTextWatcher(paidAmount0!!))
        paidAmount0?.id = paidWaterTag
        usedWater = onPeak.findViewById(R.id.textAmountViewPrice)

        val onMid = view.findViewById<View>(R.id.includeOnMid)
        val nameOnMid = onMid.findViewById<TextView>(R.id.textAmountViewName)
        nameOnMid.setText(R.string.water_waste_pay)
        paidAmount1 = onMid.findViewById(R.id.textAmountViewAmount)
        paidAmount1?.isEnabled = false
        paidAmount1?.id = paidWasteTag
        usedWaste = onMid.findViewById(R.id.textAmountViewPrice)

        val offPeak = view.findViewById<View>(R.id.includeOffPeak)
        val nameOffPeak = offPeak.findViewById<TextView>(R.id.textAmountViewName)
        nameOffPeak.setText(R.string.water_storm_pay)
        paidAmount2 = offPeak.findViewById(R.id.textAmountViewAmount)
        paidAmount2?.addTextChangedListener(AmountTextWatcher(paidAmount2!!))
        paidAmount2?.id = paidStormTag
        usedStorm = offPeak.findViewById(R.id.textAmountViewPrice)
    }

    private fun cleanUp() {
        super.initMainStatement()
        if (!doEdit!!) {
            changeDateVisibility(true)
            usedWater?.setText(R.string.water_zero_used)
            usedWaste?.setText(R.string.water_zero_used)
            usedStorm?.setText(R.string.water_zero_used)
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
                super.saveMainStatement(utility!!, Constants.WaterType)
            }
            utility!!.amountDue = super.amountFrom(paymentTotal!!)
            utility.amountType0 = super.amountFrom(paidAmount0!!)
            utility.amountType1 = super.amountFrom(paidAmount1!!)
            utility.amountType2 = super.amountFrom(paidAmount2!!)
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
                paidWaterTag -> if (entity!!.unitPrice0 > 0.0) {
                    paid /= entity!!.unitPrice0
                    usedWater?.setText(String.format("(m3) %.3f", paid))
                    val waste = paid * 0.85
                    usedWaste?.setText(String.format("(m3) %.3f", waste))
                    paidAmount1?.setText(String.format("%.2f", waste * entity!!.unitPrice1))
                }
                paidWasteTag -> if (entity!!.unitPrice1 > 0.0) {
                    paid /= entity!!.unitPrice1
                    usedWaste?.setText(String.format("(m3) %.3f", paid))
                }
                paidStormTag -> if (entity!!.unitPrice2 > 0.0) {
                    paid /= entity!!.unitPrice2
                    usedStorm?.setText(String.format("(days) %.0f", paid))
                }
            }
        }
    }

    companion object {

        internal val paidWaterTag = 111
        internal val paidWasteTag = 222
        internal val paidStormTag = 333

        fun newInstance(): WaterFragment {
            val fragment = WaterFragment()
            fragment.mTag = FragmentScreen.WATER_FRAGMENT
            fragment.entity = MyUtilitiesApplication.getConfigEntityForType(Constants.WaterType)
            return fragment
        }
    }
    //endregion
}// Required empty public constructor
