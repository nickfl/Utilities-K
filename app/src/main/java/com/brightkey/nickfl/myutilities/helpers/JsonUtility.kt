package com.brightkey.nickfl.myutilities.helpers

import android.content.Context
import com.brightkey.nickfl.myutilities.entities.ConfigEntity
import com.brightkey.nickfl.myutilities.entities.LoadUtility
import com.brightkey.nickfl.myutilities.entities.UtilityBillModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import timber.log.Timber
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

    // Prepare to store Bills in file
    fun convertToJson(list: List<UtilityBillModel>): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val listType = object : TypeToken<List<UtilityBillModel>>() {
        }.type
        val jsonContent = gson.toJson(list, listType)
        return jsonContent
    }

    // Load Records
    private fun loadJSONFromUtility(fileName: String, context: Context): List<LoadUtility>? {
        val jsonStr = loadJsonFile(fileName, context)
        val gson = Gson()
        val listType = object : TypeToken<List<LoadUtility>>() {

        }.type
        return gson.fromJson<List<LoadUtility>>(jsonStr, listType)
    }

    fun loadUtilityFromFileToRealm(fileName: String, context: Context) {
        val utility = loadJSONFromUtility(fileName, context)
        if (utility != null) {
            for (item in utility) {
                item.saveToRealm()
            }
            var line = Exception().stackTrace[0].lineNumber + 1
            Timber.d("Realm: [$line] $fileName")
        } else {
            var line = Exception().stackTrace[0].lineNumber + 1
            Timber.d("Realm: [$line] $fileName NOT Found!")
        }
    }
}
