package com.brightkey.nickfl.myutilities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.activities.MainActivity
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.entities.UtilityBillModel
import com.brightkey.nickfl.myutilities.helpers.Constants
import com.brightkey.nickfl.myutilities.helpers.RealmHelper
import timber.log.Timber
import java.util.*

class PhoneFragment : BaseFragment(), View.OnClickListener {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_phone, container, false)
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
        val acc = view.findViewById<View>(R.id.textPhoneAcc) as TextView
        acc.text = entity?.accountNumber

        addPayment = view.findViewById(R.id.buttonAddPhonePayment)
        addPayment?.setOnClickListener(this)

        // the same for all Utilities - Main Statement data
        super.setupMainStatement(view, this)

        // Details:
        val onPeak = view.findViewById<View>(R.id.includeOnPeak)
        val nameOnPeak = onPeak.findViewById<TextView>(R.id.textAmountViewName)
        nameOnPeak.setText(R.string.bell_tv_pay)
        paidAmount0 = onPeak.findViewById(R.id.textAmountViewAmount)
        var used = onPeak.findViewById<TextView>(R.id.textAmountViewPrice)
        used.text = ""

        val onMid = view.findViewById<View>(R.id.includeOnMid)
        val nameOnMid = onMid.findViewById<TextView>(R.id.textAmountViewName)
        nameOnMid.setText(R.string.bell_internet_pay)
        paidAmount1 = onMid.findViewById(R.id.textAmountViewAmount)
        used = onMid.findViewById(R.id.textAmountViewPrice)
        used.text = ""

        val offPeak = view.findViewById<View>(R.id.includeOffPeak)
        val nameOffPeak = offPeak.findViewById<TextView>(R.id.textAmountViewName)
        nameOffPeak.setText(R.string.bell_line_pay)
        paidAmount2 = offPeak.findViewById(R.id.textAmountViewAmount)
        used = offPeak.findViewById(R.id.textAmountViewPrice)
        used.text = ""
    }

    private fun cleanUp() {
        super.initMainStatement()
        if (!doEdit!!) {
            changeDateVisibility(true)
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
            val utility = if (doEdit!!) editUtility else UtilityBillModel()
            if (!doEdit!!) {
                super.saveMainStatement(utility!!, Constants.PhoneType)
            }
            utility!!.amountDue = super.amountFrom(paymentTotal!!)
            utility.amountType0 = super.amountFrom(paidAmount0!!)
            utility.amountType1 = super.amountFrom(paidAmount1!!)
            utility.amountType2 = super.amountFrom(paidAmount2!!)
            RealmHelper.updateBill(utility)
            exitListener?.onFragmentExit()
            return
        }
        currentDateView = if (v === addStatementDay) billDate else dueDate
        val newFragment = DatePickerFragment()
        newFragment.show(fragmentManager!!, "datePicker")
    }

    companion object {

        fun newInstance(): PhoneFragment {
            val fragment = PhoneFragment()
            fragment.mTag = FragmentScreen.PHONE_FRAGMENT
            fragment.entity = MyUtilitiesApplication.getConfigEntityForType(Constants.PhoneType)
            return fragment
        }
    }
    //endregion
}// Required empty public constructor
