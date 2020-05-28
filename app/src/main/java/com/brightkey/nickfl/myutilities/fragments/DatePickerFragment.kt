package com.brightkey.nickfl.myutilities.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class DatePickerFragment(parent: FragmentActivity) : DialogFragment() {

    private var parentActivity: FragmentActivity = parent

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(),
                activity as DatePickerDialog.OnDateSetListener?, year, month, day)
    }

    fun showDatePicker() {
        val transaction = parentActivity.supportFragmentManager.beginTransaction()
        this.show(transaction, "DatePicker")
    }
}
