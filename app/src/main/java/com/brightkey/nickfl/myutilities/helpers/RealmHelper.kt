package com.brightkey.nickfl.myutilities.helpers

import android.os.Bundle
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication.Companion.context
import com.brightkey.nickfl.myutilities.entities.ConfigEntity
import com.brightkey.nickfl.myutilities.entities.LoadUtility
import com.brightkey.nickfl.myutilities.entities.UtilityBillModel
import io.realm.Realm
import io.realm.kotlin.where
import timber.log.Timber
import java.io.FileInputStream

class RealmHelper private constructor() {

    fun cleanAllUtilityBills() {
        try {
            realm.executeTransaction { realm ->
                realm.deleteAll()
            }
        } catch (e: IllegalStateException) {
            var line = Exception().stackTrace[0].lineNumber + 1
            Timber.e("Realm: [$line] deleteAll Failed!")
        }
    }

    fun addUtilityBill(bill: LoadUtility) {
        var id = realm.where<UtilityBillModel>().findAll().size
        realm.executeTransaction { realm ->
            val rBill = realm.createObject(UtilityBillModel::class.java, id)
            rBill.utilityType = bill.utilityType
            rBill.datePaid = DateFormatters.dateFromString(bill.datePaid)
            rBill.dueDate = DateFormatters.dateFromString(bill.dueDate)
            rBill.billDate = DateFormatters.dateFromString(bill.billDate)
            rBill.amountDue = bill.amountDue
            rBill.amountType0 = bill.amountType0
            rBill.amountType1 = bill.amountType1
            rBill.amountType2 = bill.amountType2
            id++
        }
    }

    fun fetchAllUtilityBills(): List<UtilityBillModel> {
        val res = realm.where<UtilityBillModel>().findAll()
        var list: ArrayList<UtilityBillModel> = ArrayList()
        for (item in res) {
            list.add(item.copy())
        }
        return  list
    }

    fun saveJsonToRealm(fis: FileInputStream) {
        realm.executeTransaction { realm ->
            realm.createAllFromJson(UtilityBillModel::class.java, fis)
        }
    }

    // calculate all totals!: units and amount paid
    fun unitsForUtility(item: ConfigEntity): Bundle {
        val utils = realm.where<UtilityBillModel>()
                .equalTo("utilityType", item.utilityIcon!!)
                .findAll()
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
            val bundle = unitsForUtility(item)
            total += bundle.getDouble("total")
        }
        return total
    }

    companion object {
        private var INSTANCE: RealmHelper? = null
        private lateinit var realm: Realm
//        private lateinit var realmListener: RealmChangeListener<Realm>

        fun shared(): RealmHelper {
            if (INSTANCE == null) {
                INSTANCE = RealmHelper()
                Realm.init(context)
                realm = Realm.getDefaultInstance()
//                realmListener = RealmChangeListener {
//                    val res = realm.where<UtilityBillModel>().findAll()
//var line = Exception().stackTrace[0].lineNumber + 1
//Timber.d("Realm: [$line] CHANGED: ${res.size}")
                }
//                realm.addChangeListener(realmListener)            }
            return INSTANCE!!
        }

        fun utilitiesForType(type: String): List<UtilityBillModel> {
            val result = realm.where<UtilityBillModel>()
                    .equalTo("utilityType", type)
                    .findAll()
                    .filter { PeriodManager.shared.isDateInPeriod(it.datePaid) }
            var utils: ArrayList<UtilityBillModel> = ArrayList()
            for (item in result) {
                utils.add(item)
            }
            return utils
        }

        fun updateBill(bill: UtilityBillModel) {
            if (bill.id == -1L) {
                bill.id = realm.where<UtilityBillModel>().findAll().size.toLong()
            }
            realm.executeTransaction { realm ->
                realm.copyToRealmOrUpdate(bill)
            }
        }
    }
}