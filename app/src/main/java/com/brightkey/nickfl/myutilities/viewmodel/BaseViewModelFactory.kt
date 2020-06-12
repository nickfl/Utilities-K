package com.brightkey.nickfl.myutilities.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BaseViewModelFactory(private val arg: String) :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(String::class.java).newInstance(arg)
    }
}