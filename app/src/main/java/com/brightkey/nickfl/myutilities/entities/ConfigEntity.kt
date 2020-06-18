package com.brightkey.nickfl.myutilities.entities

class ConfigEntity {

    var utilityIcon: String? = null       // hydro_bill
    var utilityType: String? = null       // Hydro
    var utilityVendorName: String? = null // ENBRIDGE
    var vendorNameColor: String? = null   // #F58233
    var accountNumber: String? = null     // 1291-9-1-2938293
    var unitType: String? = null          // m3, kWh, ..
    var unitPrice0: Double = 0.0          // $20.0
    var unitPrice1: Double = 0.0          // $20.0
    var unitPrice2: Double = 0.0          // $20.0

    override fun toString(): String {
        return "{utilityIcon:" + utilityIcon + ", utilityType:" + utilityType +
                ", utilityVendor:" + utilityVendorName + ", accountNumber:" + accountNumber +
                ", unitType:" + unitType +
                ", price0:" + unitPrice0 + ", price1:" + unitPrice1 + ", price2:" + unitPrice2 + "}"
    }
}
