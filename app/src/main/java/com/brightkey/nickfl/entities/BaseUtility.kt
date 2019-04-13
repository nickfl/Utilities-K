package com.brightkey.nickfl.entities

import com.brightkey.nickfl.helpers.DateFormatters

import java.util.Date

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class BaseUtility {
    @Id
    var id: Long = 0
    var utilityType: String? = null // Hydro, Water, Gas
    var billDate: Date? = null
    var dueDate: Date? = null
    var datePaid: Date? = null
    var amountDue: Double = 0.toDouble()
    var amountType0: Double = 0.toDouble()
    var amountType1: Double = 0.toDouble()
    var amountType2: Double = 0.toDouble()
    val paidDate: String
        get() = if (datePaid != null) {
            DateFormatters.dateStringFromDate(datePaid!!)
        } else ""

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
}
