package com.brightkey.nickfl.myutilities.helpers

import android.content.Context
import com.brightkey.nickfl.myutilities.entities.ConfigEntity
import com.brightkey.nickfl.myutilities.entities.LoadUtility
import com.brightkey.nickfl.myutilities.entities.UtilityBillModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object JsonUtility {

    @Throws(IOException::class)
    private fun assetJSONFile(filename: String, context: Context): String {
        val file = context.assets.open(filename)
        val formArray = ByteArray(file.available())
        file.read(formArray)
        file.close()
        return String(formArray)
    }

    private fun deviceJSONFile(stream: InputStream): String {
        val sb = StringBuilder()
        val br = BufferedReader(InputStreamReader(stream))
        var line: String?

        line = br.readLine()
        while (line != null) {
            sb.append(line)
            line = br.readLine()
        }
        br.close()
        return sb.toString()
    }

    private fun loadJsonFileFromAssets(fileName: String, context: Context): String {
        return assetJSONFile(fileName, context)
    }

    // Load Config
    fun loadConfigFromAsset(fileName: String, context: Context): List<ConfigEntity>? {
        val jsonStr = loadJsonFileFromAssets(fileName, context)
        val listType = object : TypeToken<List<ConfigEntity>>() {}.type
        return Gson().fromJson<List<ConfigEntity>>(jsonStr, listType)
    }

    // Prepare to store Bills in file
    fun convertToJson(list: List<UtilityBillModel>): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val listType = object : TypeToken<List<UtilityBillModel>>() {}.type
        return gson.toJson(list, listType)
    }

    // Load Records
    private fun loadJSONFromUtilityAsset(fileName: String, context: Context): List<LoadUtility>? {
        val jsonStr = loadJsonFileFromAssets(fileName, context)
        val listType = object : TypeToken<List<LoadUtility>>() {}.type
        return Gson().fromJson<List<LoadUtility>>(jsonStr, listType)
    }

    private fun loadJSONFromInputStream(stream: InputStream): List<UtilityBillModel>? {
        val jsonStr =  deviceJSONFile(stream)
        val listType = object : TypeToken<List<UtilityBillModel>>() {}.type
        return Gson().fromJson<List<UtilityBillModel>>(jsonStr, listType)
    }

    fun loadUtilityFromFileToRealm(fileName: String, context: Context) {
        val utility = loadJSONFromUtilityAsset(fileName, context)
        utility?.let { list ->
            list.forEach { it.saveToRealm() }
            return
        }

        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.d("Realm: [$line] $fileName NOT Found!")
    }

    fun loadUtilityFromFileToRealm(stream: InputStream) {
        val utility = loadJSONFromInputStream(stream)
        utility?.let { list ->
            list.forEach { it.saveToRealm() }
            return
        }

        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.d("Realm: [$line] $stream NOT Found!")
    }
}
