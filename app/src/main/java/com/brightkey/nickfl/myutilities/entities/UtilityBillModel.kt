package com.brightkey.nickfl.myutilities.entities

import com.brightkey.nickfl.myutilities.helpers.DateFormatters
import com.brightkey.nickfl.myutilities.helpers.RealmHandled
import com.brightkey.nickfl.myutilities.helpers.RealmHelperLocal
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class UtilityBillModel(
        @PrimaryKey
        var id: Long = -1L,
        var utilityType: String = "", // Hydro_bill, Water_bill, Gas_bill, Bell_bill
        var billDate: Date? = null,
        var dueDate: Date? = null,
        var datePaid: Date? = null,
        var amountDue: Double = 0.0,
        var amountType0: Double = 0.0,
        var amountType1: Double = 0.0,
        var amountType2: Double = 0.0
) : RealmObject(), RealmHandled {

    fun copy(): UtilityBillModel {
        val dest = UtilityBillModel()
        dest.id = this.id
        dest.utilityType = this.utilityType
        dest.billDate = this.billDate
        dest.dueDate = this.dueDate
        dest.datePaid = this.datePaid
        dest.amountDue = this.amountDue
        dest.amountType0 = this.amountType0
        dest.amountType1 = this.amountType1
        dest.amountType2 = this.amountType2
        return dest
    }

    open fun getAmountDue(): String {
        return if (amountDue > 0.0) {
            "" + amountDue
        } else ""
    }

    open fun getAmountType0(): String {
        return if (amountType0 > 0.0) {
            "" + amountType0
        } else ""
    }

    open fun getAmountType1(): String {
        return if (amountType1 > 0.0) {
            "" + amountType1
        } else ""
    }

    open fun getAmountType2(): String {
        return if (amountType2 > 0.0) {
            "" + amountType2
        } else ""
    }

    open fun getBillDate(): String {
        return billDate?.let { DateFormatters.dateStringFromDate(it) } ?: ""
    }

    open fun setBillDate(billDateStr: String?) {
        billDateStr?.let{
            this.billDate = DateFormatters.dateFromString(it)
        }
    }

    open fun getDueDate(): String {
        return dueDate?.let { DateFormatters.dateStringFromDate(it) } ?: ""
    }

    open fun setDueDate(dueDateStr: String?) {
        dueDateStr?.let{
            this.dueDate = DateFormatters.dateFromString(it)
        }
    }

    open fun getDatePaid(): String {
        return datePaid?.let { DateFormatters.dateStringFromDate(it) } ?: ""
    }

    open fun setDatePaid(paidDateStr: String?) {
        paidDateStr?.let{
            this.datePaid = DateFormatters.dateFromString(it)
        }
    }

    //RealmHandled protocol
    override fun saveToRealm() {
        RealmHelperLocal().addUtilityBill(this)
    }

//    val paidDate: String
//        get() = if (datePaid != null) {
//            DateFormatters.dateStringFromDate(datePaid!!)
//        } else ""
}