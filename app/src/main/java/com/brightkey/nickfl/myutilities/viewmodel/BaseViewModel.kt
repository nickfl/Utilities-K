package com.brightkey.nickfl.myutilities.viewmodel

import android.text.Editable
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.entities.ConfigEntity
import com.brightkey.nickfl.myutilities.entities.UtilityBillModel
import com.brightkey.nickfl.myutilities.helpers.RealmHelper
import java.util.*

class BaseViewModel(): ViewModel() {

    var entity: ConfigEntity? = null
    private var editIndex = 0
    private var utilityBillToEdit = UtilityBillModel()

    constructor(billType: String): this() {
        entity = MyUtilitiesApplication.getConfigEntityForType(billType)
    }

    fun setEditIndex(index: Int) {
        editIndex = index
    }

    fun updateBillInRealm() {
        RealmHelper.updateBill(utilityBillToEdit)
    }

    fun fetchBillForUtility(utilityType: String) {
        val utils = RealmHelper.utilitiesForType(utilityType)
        val utility = utils[editIndex]
        utilityBillToEdit = utility.copy()
    }

    fun getBillDate(): String {
        return utilityBillToEdit.getBillDate()
    }
    fun getDueDate(): String {
        return utilityBillToEdit.getDueDate()
    }
    fun getAmountDue(): String {
        return utilityBillToEdit.getAmountDue()
    }
    fun getAmountType0(): String {
        return utilityBillToEdit.getAmountType0()
    }
    fun getAmountType1(): String {
        return utilityBillToEdit.getAmountType2()
    }
    fun getAmountType2(): String {
        return utilityBillToEdit.getAmountType2()
    }
    fun setAmountDue(payment: TextView) {
        utilityBillToEdit.amountDue = amountFrom(payment)
    }
    fun setAmountType0(payment: TextView) {
        utilityBillToEdit.amountType0 = amountFrom(payment)
    }
    fun setAmountType1(payment: TextView) {
        utilityBillToEdit.amountType1 = amountFrom(payment)
    }
    fun setAmountType2(payment: TextView) {
        utilityBillToEdit.amountType2 = amountFrom(payment)
    }
    fun setUtilityType(type: String) {
        utilityBillToEdit.utilityType = type
    }
    fun setUtilityDatePaid(date: Date) {
        utilityBillToEdit.datePaid = date
    }
    fun setUtilityDueDate(date: Date) {
        utilityBillToEdit.dueDate = date
    }
    fun setUtilityBillDate(date: Date) {
        utilityBillToEdit.billDate = date
    }

    fun validateData(fields: MutableList<EditText>): Boolean {
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

    fun editableChanged(editable: Editable): Double {
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

    companion object {

        //region start Helpers
        private fun amountFrom(payment: TextView): Double {
            var text = payment.text.toString()
            if (text[0] == '$') {
                text = text.substring(1)
            }
            return java.lang.Double.parseDouble(text)
        }
        //endregion

    }
}
