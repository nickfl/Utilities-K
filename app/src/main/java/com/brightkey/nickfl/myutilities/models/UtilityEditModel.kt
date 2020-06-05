package com.brightkey.nickfl.myutilities.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UtilityEditModel(var index: Int = 0,
                       var edit: Boolean = false
) : Parcelable