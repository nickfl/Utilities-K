package com.brightkey.nickfl.myutilities.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class UtilityEditModel(var index: Int = 0,
                       var edit: Boolean = false
) : Parcelable