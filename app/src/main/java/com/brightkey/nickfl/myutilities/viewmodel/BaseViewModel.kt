package com.brightkey.nickfl.myutilities.viewmodel

import android.text.Editable
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.entities.ConfigEntity
import com.brightkey.nickfl.myutilities.entities.UtilityBillModel
import com.brightkey.nickfl.myutilities.helpers.RealmHelper
import com.brightkey.nickfl.myutilities.helpers.RealmHelperLocal
import java.util.*

class BaseViewModel(billType: String): ViewModel() {

    private var editIndex: Int = 0
    private var entity: ConfigEntity? = null

    private var utilityBillToEdit = UtilityBillModel()
    private var unitPrice0: Double? = null
    private var unitPrice1: Double? = null
    private var unitPrice2: Double? = null

    init {
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

    fun fetchBill() {
        val type = if (utilityType.isEmpty()) entity?.utilityIcon else utilityType
        type?.let {
            val utils = RealmHelper.utilitiesForType(it)
            val utility = utils[editIndex]
            utilityBillToEdit = utility.copy()
            updateValues()
        }
    }

    var utilityType: String = ""
        get() = utilityBillToEdit.utilityType
        set(value) {
            field = value
            utilityBillToEdit.utilityType = value
        }

    private val _brandBillType = MutableLiveData("Utility")
    val brandBillType: LiveData<String> = _brandBillType

    private val _accountNumber = MutableLiveData("111 222 333")
    val accountNumber: LiveData<String?> = _accountNumber

    private val _billDate = MutableLiveData("Jan 01, 2020")
    val billDate: LiveData<String> = _billDate

    private val _dueDate = MutableLiveData("Jan 01, 2020")
    val dueDate: LiveData<String> = _dueDate

    private val _amountDue = MutableLiveData("0.0")
    val amountDue: LiveData<String> = _amountDue

    private val _amountType0 = MutableLiveData("0.0")
    val amountType0: LiveData<String> = _amountType0

    private val _amountType1 = MutableLiveData("0.0")
    val amountType1: LiveData<String> = _amountType1

    private val _amountType2 = MutableLiveData("0.0")
    val amountType2: LiveData<String> = _amountType2

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

    private fun updateValues() {
        _accountNumber.value = entity?.accountNumber
        _brandBillType.value = entity?.utilityType
        _dueDate.value = utilityBillToEdit.getDueDate()
        _billDate.value = utilityBillToEdit.getBillDate()
        _amountDue.value = utilityBillToEdit.getAmountDue()
        _amountType0.value = utilityBillToEdit.getAmountType0()
        _amountType1.value = utilityBillToEdit.getAmountType1()
        _amountType2.value = utilityBillToEdit.getAmountType2()
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
        return RealmHelperLocal().deleteUtilityBill(utilityBillToEdit)
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
