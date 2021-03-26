package com.brightkey.nickfl.myutilities.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ChartModel(var type: String,
                 var color: String
) : Parcelable
