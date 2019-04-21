package com.brightkey.nickfl.entities

import com.brightkey.nickfl.helpers.DateFormatters
import com.brightkey.nickfl.helpers.ObjectBoxHelper

class LoadUtility internal constructor(var utilityType: String, // Hydro, Water, Gas, Bell
                                       var billDate: String, var dueDate: String, var datePaid: String, var amountDue: Double,
                                       var amountType0: Double, var amountType1: Double, var amountType2: Double) {

    fun saveToBox() {
        val utility = BaseUtility()
        utility.utilityType = this.utilityType
        utility.datePaid = DateFormatters.dateFromString(this.datePaid)
        utility.dueDate = DateFormatters.dateFromString(this.dueDate)
        utility.billDate = DateFormatters.dateFromString(this.billDate)
        utility.amountDue = this.amountDue
        utility.amountType0 = this.amountType0
        utility.amountType1 = this.amountType1
        utility.amountType2 = this.amountType2
        val utilityBox = ObjectBoxHelper.shared().utilityBox
        utilityBox!!.put(utility)
    }

    companion object {

        fun storeRecordsInBox(utilities: List<BaseUtility>) {
            for (item in utilities) {
                val billDate = DateFormatters.dateStringFromDate(item.billDate!!)
                val dueDate = DateFormatters.dateStringFromDate(item.dueDate!!)
                val datePaid = DateFormatters.dateStringFromDate(item.datePaid!!)
                val data = LoadUtility(item.utilityType!!, billDate, dueDate, datePaid,
                        item.amountDue, item.amountType0, item.amountType1, item.amountType2)
                data.saveToBox()
            }
        }
    }
}
