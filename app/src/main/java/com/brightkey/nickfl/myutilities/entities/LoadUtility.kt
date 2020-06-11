package com.brightkey.nickfl.myutilities.entities

import com.brightkey.nickfl.myutilities.helpers.RealmHandled
import com.brightkey.nickfl.myutilities.helpers.RealmHelper
import java.io.FileInputStream

class LoadUtility internal constructor(var utilityType: String, // Hydro, Water, Gas, Bell
                                       var billDate: String,
                                       var dueDate: String,
                                       var datePaid: String,
                                       var amountDue: Double,
                                       var amountType0: Double,
                                       var amountType1: Double,
                                       var amountType2: Double) : RealmHandled {

    override fun saveToRealm() {
        val helper = RealmHelper.shared()
        helper.addUtilityBill(this)
    }

    companion object {

        fun storeRecordsInRealm(fis: FileInputStream) {
            RealmHelper.shared().saveJsonToRealm(fis)
        }
    }
}
