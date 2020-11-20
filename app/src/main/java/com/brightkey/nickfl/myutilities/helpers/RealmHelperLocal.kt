package com.brightkey.nickfl.myutilities.helpers

import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.entities.UtilityBillModel
import io.realm.Realm
import io.realm.kotlin.where
import timber.log.Timber

class RealmHelperLocal {

    private val realm: Realm

    init {
        Realm.init(MyUtilitiesApplication.context)
        realm = Realm.getDefaultInstance()
    }

    fun cleanAllUtilityBills() {
        try {
            realm.executeTransactionAsync { realm ->
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

    fun fetchAllUtilityBills(): List<UtilityBillModel> {
        val res = realm.where<UtilityBillModel>().findAll()
        val list: ArrayList<UtilityBillModel> = ArrayList()
        res.forEach { list.add(it.copy()) }
        return list
    }
}