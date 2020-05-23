package com.brightkey.nickfl.myutilities.helpers

import android.os.Bundle
import com.brightkey.nickfl.myutilities.application.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.entities.BaseUtility
import com.brightkey.nickfl.myutilities.entities.BaseUtility_
import com.brightkey.nickfl.myutilities.entities.ConfigEntity
import com.brightkey.nickfl.myutilities.entities.MyObjectBox
import io.objectbox.Box
import io.objectbox.BoxStore

class ObjectBoxHelper private constructor() {
    var utilityBox: Box<BaseUtility>? = null
        private set

    val isBillsLoaded: Boolean
        get() {
            val utils = utilityBox!!.query().build().find()
            return utils.size > 0
        }

    fun cleanUtilityBox() {
        utilityBox!!.removeAll()
    }

    fun allBills(): List<BaseUtility> {
        return utilityBox!!.query().build().find()
    }

    // calculate all totals!: units and amount paid
    fun unitsForUtility(item: ConfigEntity): Bundle {
        val utils = utilityBox!!.query().equal(BaseUtility_.utilityType, item.utilityIcon!!).build().find()
                                .filter { PeriodManager.shared.isDateInPeriod(it.datePaid) }
        var units: Long = 0
        var total = 0.0
        for (one in utils) {
            if (item.unitPrice0 > 0.0)
                units += (one.amountType0 / item.unitPrice0).toLong()
            if (item.unitPrice1 > 0.0)
                units += (one.amountType1 / item.unitPrice1).toLong()
            if (item.unitPrice2 > 0.0)
                units += (one.amountType2 / item.unitPrice2).toLong()
            total += one.amountDue
        }
        val bundle = Bundle()
        bundle.putLong("units", units)
        bundle.putDouble("total", total)
        return bundle
    }

    fun totalPayment(): Double {
        var total = 0.0
        for (item in MyUtilitiesApplication.config!!) {
            val bundle = ObjectBoxHelper.shared().unitsForUtility(item)
            total += bundle.getDouble("total")
        }
        return total
    }

    companion object {

        private var INSTANCE: ObjectBoxHelper? = null
        private var boxStore: BoxStore? = null

        fun shared(): ObjectBoxHelper {
            if (INSTANCE == null) {
                INSTANCE = ObjectBoxHelper()
                val mContext = MyUtilitiesApplication.context
                boxStore = MyObjectBox.builder().androidContext(mContext!!).build()
                INSTANCE!!.utilityBox = boxStore!!.boxFor(BaseUtility::class.java)
            }
            return INSTANCE!!
        }
    }
}
