package com.brightkey.nickfl.myutilities.entities

import com.brightkey.nickfl.myutilities.helpers.DateFormatters
import com.brightkey.nickfl.myutilities.helpers.RealmHandled
import com.brightkey.nickfl.myutilities.helpers.RealmHelper
import java.io.FileInputStream

class LoadUtility internal constructor(var utilityType: String, // Hydro_bill, Water_bill, Gas_bill, Bell_bill
                                       var billDate: String,
                                       var dueDate: String,
                                       var datePaid: String,
                                       var amountDue: Double,
                                       var amountType0: Double,
                                       var amountType1: Double,
                                       var amountType2: Double) : RealmHandled {

    override fun saveToRealm() {
        val helper = RealmHelper.shared()
        helper.addUtilityBill(convertToBillModel())
    }

    private fun convertToBillModel(): UtilityBillModel {
        val model = UtilityBillModel()
        model.utilityType = this.utilityType
        model.datePaid = DateFormatters.dateFromString(this.datePaid)
        model.dueDate = DateFormatters.dateFromString(this.dueDate)
        model.billDate = DateFormatters.dateFromString(this.billDate)
        model.amountDue = this.amountDue
        model.amountType0 = this.amountType0
        model.amountType1 = this.amountType1
        model.amountType2 = this.amountType2
        return model
    }

    companion object {

        fun storeRecordsInRealm(fis: FileInputStream) {
            RealmHelper.shared().saveJsonToRealm(fis)
        }
    }
}
