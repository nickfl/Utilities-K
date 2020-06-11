package com.brightkey.nickfl.myutilities.viewmodel

import androidx.lifecycle.ViewModel
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.entities.ConfigEntity
import com.brightkey.nickfl.myutilities.entities.UtilityBillModel
import com.brightkey.nickfl.myutilities.helpers.Constants
import com.brightkey.nickfl.myutilities.helpers.DateFormatters
import com.brightkey.nickfl.myutilities.helpers.RealmHelper
import com.brightkey.nickfl.myutilities.models.UtilityEditModel
import java.util.*

open class BaseViewModel(billType: String // Constant.HeatType
): ViewModel() {

    open val entity = if (billType.isNotEmpty()) MyUtilitiesApplication.getConfigEntityForType(billType) else null
    open var editIndex = 0
    open var doEdit = false
    open var editUtility = UtilityBillModel()

    open fun billForUtility(utilityType: String, index: Int) {
        val utils = RealmHelper.utilitiesForType(utilityType)
        val utility = utils[index]
        editUtility = utility.copy()
    }
}
