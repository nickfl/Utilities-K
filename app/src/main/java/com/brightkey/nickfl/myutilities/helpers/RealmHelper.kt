package com.brightkey.nickfl.myutilities.helpers

import android.os.Bundle
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication.Companion.context
import com.brightkey.nickfl.myutilities.entities.ConfigEntity
import com.brightkey.nickfl.myutilities.entities.UtilityBillModel
import io.realm.Realm
import io.realm.kotlin.where
import timber.log.Timber
import java.io.FileInputStream

interface RealmHandled {
    fun saveToRealm()
}

class RealmHelper private constructor() {

    fun cleanAllUtilityBills() {
        try {
            realm.executeTransaction { realm ->
                realm.deleteAll()
            }
        } catch (e: IllegalStateException) {
            val line = Exception().stackTrace[0].lineNumber + 1
            Timber.e("Realm: [$line] deleteAll Failed!")
        }
    }

    fun addUtilityBill(bill: UtilityBillModel) {
        realm.executeTransaction { realm ->
            val id = if (bill.id < 0) realm.where<UtilityBillModel>().findAll().size.toLong() else bill.id
            val rBill = realm.createObject(UtilityBillModel::class.java, id)
            rBill.utilityType = bill.utilityType
            rBill.datePaid = bill.datePaid
            rBill.dueDate = bill.dueDate
            rBill.billDate = bill.billDate
            rBill.amountDue = bill.amountDue
            rBill.amountType0 = bill.amountType0
            rBill.amountType1 = bill.amountType1
            rBill.amountType2 = bill.amountType2
        }
    }

    fun fetchAllUtilityBills(): List<UtilityBillModel> {
        val res = realm.where<UtilityBillModel>().findAll()
        val list: ArrayList<UtilityBillModel> = ArrayList()
        res.forEach { list.add(it.copy()) }
        return  list
    }

    fun deleteUtilityBill(bill: UtilityBillModel): Int {
        val res = realm.where<UtilityBillModel>().findAll().first { it.id == bill.id }
        realm.executeTransaction {
            res?.deleteFromRealm()
        }
        return realm.where<UtilityBillModel>()
                .equalTo("utilityType", bill.utilityType)
                .findAll()
                .filter { PeriodManager.shared.isDateInPeriod(it.datePaid) }.size
    }

    fun saveJsonToRealm(fis: FileInputStream) {
        JsonUtility.loadUtilityFromFileToRealm(fis)
    }

    // calculate all totals!: units and amount paid
    fun unitsForUtility(item: ConfigEntity): Bundle {
        val utils = realm.where<UtilityBillModel>()
                .equalTo("utilityType", item.utilityIcon!!)
                .findAll()
                .filter { PeriodManager.shared.isDateInPeriod(it.datePaid) }
        var units = 0L
        var total = 0.0
        utils.forEach {
            if (item.unitPrice0 > 0.0)
                units += (it.amountType0 / item.unitPrice0).toLong()
            if (item.unitPrice1 > 0.0)
                units += (it.amountType1 / item.unitPrice1).toLong()
            if (item.unitPrice2 > 0.0)
                units += (it.amountType2 / item.unitPrice2).toLong()
            total += it.amountDue
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
//                val config = RealmConfiguration.Builder().name("myUtilities.realm").build()
//                Realm.setDefaultConfiguration(config)
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
            val utils: ArrayList<UtilityBillModel> = ArrayList()
            result.forEach { utils.add(it) }
            return utils
        }

        fun updateBill(bill: UtilityBillModel) {
            if (bill.id < 0) {
                bill.id = realm.where<UtilityBillModel>().findAll().size.toLong()
            }
            realm.executeTransaction { realm ->
                realm.copyToRealmOrUpdate(bill)
            }
        }
    }
}