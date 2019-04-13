package com.brightkey.nickfl.helpers

import android.content.Context
import android.content.res.AssetManager

import com.brightkey.nickfl.entities.ConfigEntity
import com.brightkey.nickfl.entities.LoadUtility
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Type

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

    private fun loadJsonFile(fileName: String, context: Context): String? {
        try {
            return JsonUtility.assetJSONFile(fileName, context)
        } catch (ex: IOException) {
            return null
        }

    }

    // Load Config
    fun loadConfigFromAsset(fileName: String, context: Context): List<ConfigEntity>? {
        val jsonStr = JsonUtility.loadJsonFile(fileName, context) ?: return null

        val gson = Gson()
        val listType = object : TypeToken<List<ConfigEntity>>() {

        }.type
        return gson.fromJson<List<ConfigEntity>>(jsonStr, listType)
    }

    // Load Records
    private fun loadJSONFromUtility(fileName: String, context: Context): List<LoadUtility>? {
        val jsonStr = JsonUtility.loadJsonFile(fileName, context) ?: return null
        val gson = Gson()
        val listType = object : TypeToken<List<LoadUtility>>() {

        }.type
        return gson.fromJson<List<LoadUtility>>(jsonStr, listType)
    }

    fun loadUtilityFromFile(fileName: String, context: Context) {
        val utility = JsonUtility.loadJSONFromUtility(fileName, context)
        if (utility != null) {
            for (item in utility) {
                item.saveToBox()
            }
        }
    }
}
