package com.brightkey.nickfl.myutilities.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.activities.MainActivity
import com.brightkey.nickfl.myutilities.adapters.ExitFragmentListener
import com.brightkey.nickfl.myutilities.databinding.FragmentPhoneBinding
import com.brightkey.nickfl.myutilities.helpers.Constants
import com.brightkey.nickfl.myutilities.helpers.DateFormatters
import timber.log.Timber

class PhoneFragment : BaseEditFragment(Constants.PhoneType), View.OnClickListener {

    private var _binding: FragmentPhoneBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTag = FragmentScreen.PHONE_FRAGMENT
        super.getArguments(arguments)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPhoneBinding.inflate(inflater, container, false)
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
        activity?.invalidateOptionsMenu()
        (activity as MainActivity).setCustomOptions(R.menu.fragment, getString(R.string.utility_phone_details))
        startUp()
    }

    //region start Helpers
    private fun setup(view: View) {
        setupMainStatement(view, this)
        addPayment.setOnClickListener(this)
    }

    private fun setupBindings(bindings: FragmentPhoneBinding) {
        paymentTotal = bindings.includeStatementData.layoutPrice.textPriceViewAmount
        paidAmount0 = bindings.includeOnPeak.textAmountViewAmount
        bindings.includeOnPeak.textAmountViewName.setText(R.string.bell_tv_pay)
        paidAmount1 = bindings.includeOnMid.textAmountViewAmount
        bindings.includeOnMid.textAmountViewName.setText(R.string.bell_internet_pay)
        paidAmount2 = bindings.includeOffPeak.textAmountViewAmount
        bindings.includeOffPeak.textAmountViewName.setText(R.string.bell_line_pay)
        addPayment = bindings.buttonAddPhonePayment
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
    //endregion

}// Required empty public constructor
