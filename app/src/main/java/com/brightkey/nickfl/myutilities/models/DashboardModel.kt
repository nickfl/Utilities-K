package com.brightkey.nickfl.myutilities.models

import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.entities.ConfigEntity
import com.brightkey.nickfl.myutilities.helpers.Constants
import com.brightkey.nickfl.myutilities.helpers.RealmHelper
import java.util.*
import kotlin.collections.ArrayList

// Model to present Accounts on the DashBoard
class DashboardModel internal constructor(var utilityIcon: String         // hydro_bill
                                          , var utilityType: String       // Hydro
                                          , var utilityVendor: String     // ENBRIDGE
                                          , var vendorColor: String       // #F58233
                                          , var accountNumber: String     // 1291-9-1-2938293
                                          , var unitType: String          // L, kWatt, ..
                                          , var totalUnits: Long          // 20 kW
                                          , var totalPaid: Double         // $281.0
) {
    override fun toString(): String {
        return "{utilityType:" + utilityType + ", utilityVendor:" + utilityVendor +
                ", accountNumber:" + accountNumber + ", unitType:" + unitType +
                ", totalUnits:" + totalUnits + ", totalPaid:" + totalPaid + "}"
    }

    fun iconResource(): Int {
        val iconStr = utilityIcon.lowercase()
        if (iconStr.contains(Constants.HydroType)) {
            return R.drawable.hydro_bill
        }
        if (iconStr.contains(Constants.WaterType)) {
            return R.drawable.water_bill
        }
        if (iconStr.contains(Constants.PhoneType)) {
            return R.drawable.phone_bill
        }
        return R.drawable.heat_bill
    }

    fun resetData() {
        totalUnits = 0
        totalPaid = 0.0
    }

    companion object {

        // returns array of DashboardModel, i.s. Hydro, Gas, Bell...
        fun convertToDash(config: List<ConfigEntity>): List<DashboardModel> {
            val model = ArrayList<DashboardModel>()
            for (item in config) {
                val bundle = RealmHelper.shared().unitsForUtility(item)
                val one = DashboardModel(item.utilityIcon!!, item.utilityType!!, item.utilityVendorName!!,
                        item.vendorNameColor!!, item.accountNumber!!, item.unitType!!,
                        bundle.getLong("units"), bundle.getDouble("total"))
                model.add(one)
            }
            return model
        }
    }
}