package com.brightkey.nickfl.models

import com.brightkey.nickfl.entities.BaseUtility
import com.brightkey.nickfl.entities.ConfigEntity
import com.brightkey.nickfl.fragments.FragmentScreen
import com.brightkey.nickfl.helpers.Constants
import com.brightkey.nickfl.helpers.DateFormatters
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication

import java.util.ArrayList

class TimeListModel internal constructor(var utilityIcon: String  // hydro_bill
                                         , var utilityColor: String // #5F8233
                                         , var duePaid: String      // due date to pay
                                         , var nowPaid: Double      // paid this month
                                         , var totalPaid: Double    // total paid this year
                                         , var whenPaid: String) {

    override fun toString(): String {
        return "{utilityIcon:" + utilityIcon + ", nowPaid:" + nowPaid + ", totalPaid:" + totalPaid + "whenPaid:" + whenPaid + "}"
    }

    fun screenType(): FragmentScreen {
        val iconStr = this.utilityIcon.toLowerCase()
        if (iconStr.contains(Constants.HydroType)) {
            return FragmentScreen.HYDRO_FRAGMENT
        }
        if (iconStr.contains(Constants.WaterType)) {
            return FragmentScreen.WATER_FRAGMENT
        }
        return if (iconStr.contains(Constants.PhoneType)) {
            FragmentScreen.PHONE_FRAGMENT
        } else FragmentScreen.HEAT_FRAGMENT
    }

    companion object {

        fun convertToTimeList(utilities: List<BaseUtility>, itemType: String): List<TimeListModel> {
            val entity = MyUtilitiesApplication.getConfigEntityForType(itemType)
            val models = ArrayList<TimeListModel>()
            var total = 0.0
            for (one in utilities) {
                val dueToPay = DateFormatters.dateStringFromDate(one.dueDate!!)
                val whenPaid = DateFormatters.dateStringFromDate(one.datePaid!!)
                total += one.amountDue
                val data = TimeListModel(itemType, entity!!.vendorNameColor!!, dueToPay, one.amountDue, total, whenPaid)
                models.add(0, data)
            }
            return models
        }
    }

}
