package com.brightkey.nickfl.myutilities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.activities.MainActivity
import com.brightkey.nickfl.myutilities.helpers.Constants
import com.brightkey.nickfl.myutilities.models.UtilityEditModel
import timber.log.Timber

class PhoneFragment : BaseFragment(Constants.PhoneType), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTag = FragmentScreen.PHONE_FRAGMENT
        val model = arguments?.getParcelable<UtilityEditModel>("editBillBell")
        model?.let{
            doEdit = it.edit
            editIndex = it.index
        }
        setHasOptionsMenu(true)
    }

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
        addPayment.setOnClickListener(this)

        // the same for all Utilities - Main Statement data
        setupMainStatement(view, this)

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
        initMainStatement()
        if (!doEdit) {
            changeDateVisibility(true)
        } else {
            editUtility = billForUtility(entity!!, editIndex)
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
    //endregion

}// Required empty public constructor
