package com.brightkey.nickfl.models

import com.brightkey.nickfl.entities.ConfigEntity
import com.brightkey.nickfl.helpers.Constants
import com.brightkey.nickfl.helpers.ObjectBoxHelper
import com.brightkey.nickfl.myutilities.R
import java.util.*

class DashboardModel internal constructor(var utilityIcon: String       // hydro_bill
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
        val iconStr = this.utilityIcon.toLowerCase()
        if (iconStr.contains(Constants.HydroType)) {
            return R.drawable.hydro_bill
        }
        if (iconStr.contains(Constants.WaterType)) {
            return R.drawable.water_bill
        }
        return if (iconStr.contains(Constants.PhoneType)) {
            R.drawable.phone_bill
        } else R.drawable.heat_bill
    }

    fun resetData() {
        this.totalUnits = 0
        this.totalPaid = 0.0
    }

    companion object {

        fun convertToDash(config: List<ConfigEntity>): List<DashboardModel> {
            val model = ArrayList<DashboardModel>()
            for (item in config) {
                val bundle = ObjectBoxHelper.shared().unitsForUtility(item)
                val one = DashboardModel(item.utilityIcon!!, item.utilityType!!, item.utilityVendorName!!,
                        item.vendorNameColor!!, item.accountNumber!!, item.unitType!!,
                        bundle.getLong("units"), bundle.getDouble("total"))
                model.add(one)
            }
            return model
        }
    }
}
