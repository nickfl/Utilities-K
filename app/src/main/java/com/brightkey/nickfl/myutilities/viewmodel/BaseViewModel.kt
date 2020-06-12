package com.brightkey.nickfl.myutilities.viewmodel

import android.text.Editable
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.entities.UtilityBillModel
import com.brightkey.nickfl.myutilities.helpers.RealmHelper
import java.util.*

open class BaseViewModel(billType: String // Constant.HeatType
): ViewModel() {

    open val entity = if (billType.isNotEmpty()) MyUtilitiesApplication.getConfigEntityForType(billType) else null
    private var editIndex = 0

    private var utilityBillToEdit = UtilityBillModel()

    open fun setEditIndex(index: Int) {
        editIndex = index
    }

    open fun updateBillInRealm() {
        RealmHelper.updateBill(utilityBillToEdit)
    }

    open fun fetchBillForUtility(utilityType: String) {
        val utils = RealmHelper.utilitiesForType(utilityType)
        val utility = utils[editIndex]
        utilityBillToEdit = utility.copy()
    }

    open fun getBillDate(): String {
        return utilityBillToEdit.getBillDate()
    }
    open fun getDueDate(): String {
        return utilityBillToEdit.getDueDate()
    }
    open fun getAmountDue(): String {
        return utilityBillToEdit.getAmountDue()
    }
    open fun getAmountType0(): String {
        return utilityBillToEdit.getAmountType0()
    }
    open fun getAmountType1(): String {
        return utilityBillToEdit.getAmountType2()
    }
    open fun getAmountType2(): String {
        return utilityBillToEdit.getAmountType2()
    }
    open fun setAmountDue(payment: TextView) {
        utilityBillToEdit.amountDue = amountFrom(payment)
    }
    open fun setAmountType0(payment: TextView) {
        utilityBillToEdit.amountType0 = amountFrom(payment)
    }
    open fun setAmountType1(payment: TextView) {
        utilityBillToEdit.amountType1 = amountFrom(payment)
    }
    open fun setAmountType2(payment: TextView) {
        utilityBillToEdit.amountType2 = amountFrom(payment)
    }
    open fun setUtilityType(type: String) {
        utilityBillToEdit.utilityType = type
    }
    open fun setUtilityDatePaid(date: Date) {
        utilityBillToEdit.datePaid = date
    }
    open fun setUtilityDueDate(date: Date) {
        utilityBillToEdit.dueDate = date
    }
    open fun setUtilityBillDate(date: Date) {
        utilityBillToEdit.billDate = date
    }

    private fun amountFrom(payment: TextView): Double {
        var text = payment.text.toString()
        if (text[0] == '$') {
            text = text.substring(1)
        }
        return java.lang.Double.parseDouble(text)
    }

    open fun validateData(fields: MutableList<EditText>): Boolean {
        fields.forEach {
            if (it.text.isNullOrBlank()) { return false }
            try {
                java.lang.Double.parseDouble(it.text.toString())
            } catch (ex: Exception) {
                return false
            }
        }
        return true
    }

    open fun editableChanged(editable: Editable): Double {
        entity?.let {
            if (it.unitPrice0 > 0.0) {
                val value = editable.toString()
                if (value.isNotEmpty()) {
                    return java.lang.Double.parseDouble(value) / it.unitPrice0
                }
            }
        }
        return 0.0
    }
}
