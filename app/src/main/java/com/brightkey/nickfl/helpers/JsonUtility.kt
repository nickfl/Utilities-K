package com.brightkey.nickfl.helpers

import android.content.Context
import com.brightkey.nickfl.entities.ConfigEntity
import com.brightkey.nickfl.entities.LoadUtility
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

object JsonUtility {

    @Throws(IOException::class)
    private fun assetJSONFile(filename: String, context: Context): String {
        val manager = context.assets
        val file = manager.open(filename)
        val formArray = ByteArray(file.available())
        file.read(formArray)
        file.close()
        return String(formArray)
    }

    private fun loadJsonFile(fileName: String, context: Context): String {
        return assetJSONFile(fileName, context)
    }

    // Load Config
    fun loadConfigFromAsset(fileName: String, context: Context): List<ConfigEntity>? {
        val jsonStr = loadJsonFile(fileName, context)

        val gson = Gson()
        val listType = object : TypeToken<List<ConfigEntity>>() {

        }.type
        return gson.fromJson<List<ConfigEntity>>(jsonStr, listType)
    }

    // Load Records
    private fun loadJSONFromUtility(fileName: String, context: Context): List<LoadUtility>? {
        val jsonStr = loadJsonFile(fileName, context)
        val gson = Gson()
        val listType = object : TypeToken<List<LoadUtility>>() {

        }.type
        return gson.fromJson<List<LoadUtility>>(jsonStr, listType)
    }

    fun loadUtilityFromFile(fileName: String, context: Context) {
        val utility = loadJSONFromUtility(fileName, context)
        if (utility != null) {
            for (item in utility) {
                item.saveToBox()
            }
        }
    }
}
