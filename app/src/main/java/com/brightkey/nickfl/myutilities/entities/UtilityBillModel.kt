package com.brightkey.nickfl.myutilities.entities

import com.brightkey.nickfl.myutilities.helpers.DateFormatters
import io.realm.RealmObject
import java.util.*

open class UtilityBillModel(
        var utilityType: String = "", // Hydro, Water, Gas, Bell
        var billDate: Date? = null,
        var dueDate: Date? = null,
        var datePaid: Date? = null,
        var amountDue: Double = 0.0,
        var amountType0: Double = 0.0,
        var amountType1: Double = 0.0,
        var amountType2: Double = 0.0
) : RealmObject() {


    fun getAmountDue(): String {
        return if (amountDue > 0.0) {
            "" + amountDue
        } else ""
    }

    fun getAmountType0(): String {
        return if (amountType0 > 0.0) {
            "" + amountType0
        } else ""
    }

    fun getAmountType1(): String {
        return if (amountType1 > 0.0) {
            "" + amountType1
        } else ""
    }

    fun getAmountType2(): String {
        return if (amountType2 > 0.0) {
            "" + amountType2
        } else ""
    }

    fun getBillDate(): String {
        return if (billDate != null) {
            DateFormatters.dateStringFromDate(billDate!!)
        } else ""
    }

    fun getDueDate(): String {
        return if (dueDate != null) {
            DateFormatters.dateStringFromDate(dueDate!!)
        } else ""
    }

    val paidDate: String
        get() = if (datePaid != null) {
            DateFormatters.dateStringFromDate(datePaid!!)
        } else ""
}