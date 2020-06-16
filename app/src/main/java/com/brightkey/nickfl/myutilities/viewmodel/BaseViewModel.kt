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
    private var unitPrice0: Double? = null
    private var unitPrice1: Double? = null
    private var unitPrice2: Double? = null

    constructor(billType: String): this() {
        entity = MyUtilitiesApplication.getConfigEntityForType(billType)
        unitPrice0 = entity?.unitPrice0
        unitPrice1 = entity?.unitPrice1
        unitPrice2 = entity?.unitPrice2
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

    val accountNumber: String
        get() = entity?.accountNumber ?: ""

    var utilityType: String = ""
        get() = utilityBillToEdit.utilityType
        set(value) {
            field = value
            utilityBillToEdit.utilityType = value
        }

    var billDate: String = ""
        get() = utilityBillToEdit.getBillDate()

    var dueDate: String = ""
        get() = utilityBillToEdit.getDueDate()

    var amountDue: String = ""
        get() = utilityBillToEdit.getAmountDue()

    var amountType0: String = ""
        get() = utilityBillToEdit.getAmountType0()

    var amountType1: String = ""
        get() = utilityBillToEdit.getAmountType1()

    var amountType2: String =""
        get() = utilityBillToEdit.getAmountType2()

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
        unitPrice0?.let {
            if (it > 0.0) {
                val value = editable.toString()
                if (value.isNotEmpty()) {
                    return java.lang.Double.parseDouble(value) / it
                }
            }
        }
        return 0.0
    }

    fun unit0(paid: Double): Double {
        unitPrice0?.let {
            if (it > 0.0) {
                return paid / it
            }
        }
        return paid
    }

    fun unit1(paid: Double): Double {
        unitPrice1?.let {
            if (it > 0.0) {
                return paid / it
            }
        }
        return paid
    }

    fun unit2(paid: Double): Double {
        unitPrice2?.let {
            if (it > 0.0) {
                return paid / it
            }
        }
        return paid
    }

    fun unitWastePaid(waste: Double): Double {
        unitPrice1?.let {
            if (it > 0.0) {
                return waste * it
            }
        }
        return 0.0
    }

    fun removeCurrentBill(): Int {
        return RealmHelper.shared().deleteUtilityBill(utilityBillToEdit)
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
