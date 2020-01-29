package com.brightkey.nickfl.helpers

import android.app.Activity
import android.os.Environment
import com.brightkey.nickfl.entities.BaseUtility
import com.brightkey.nickfl.entities.LoadUtility
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.io.*

class ImportExportRecords {
    //endregion

    /* Checks if external storage is available for read and write */
    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return if (Environment.MEDIA_MOUNTED == state) {
                true
            } else false
        }

    /* Checks if external storage is available to at least read */
    private val isExternalStorageReadable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return if (Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state) {
                true
            } else false
        }

    companion object {

        //region Import records from Device
        fun importRecords(activity: Activity): Boolean {

            // remove old recode first
            ObjectBoxHelper.shared().cleanUtilityBox()

            val folder = getPublicDownloadsStorageDir(Constants.folderRecordsName) ?: return false
            return readRecords(folder, Constants.fileRecordsName)
        }

        private fun readRecords(folder: File, filename: String): Boolean {
            val file = File(folder, filename)
            if (!file.exists()) {
                return false
            }
            try {
                val fis = FileInputStream(file)
                val `in` = DataInputStream(fis)
                val formArray = ByteArray(`in`.available())
                `in`.read(formArray)
                `in`.close()
                val myData = String(formArray)
                val gson = Gson()
                val listType = object : TypeToken<List<BaseUtility>>() {

                }.type
                val records = gson.fromJson<List<BaseUtility>>(myData, listType)
                LoadUtility.storeRecordsInBox(records)
            } catch (ex: FileNotFoundException) {
                return false
            } catch (ex: IOException) {
                return false
            }

            return true
        }
        //endregion

        //region Export records to Device
        fun exportRecords(activity: Activity): Boolean {

            val folder = getPublicDownloadsStorageDir(Constants.folderRecordsName) ?: return false
            val file = File(folder, Constants.fileRecordsName)
//export as json
            val gson = GsonBuilder().setPrettyPrinting().create()
            val listType = object : TypeToken<List<BaseUtility>>() {
            }.type
            val list = ObjectBoxHelper.shared().allBills()
            val jsonContent = gson.toJson(list, listType)
            try {
                val out = FileOutputStream(file)
                out.write(jsonContent.toByteArray())
                out.close()
            } catch (ex: IOException) {
                return false
            }

            return true
        }

        private fun getPublicDownloadsStorageDir(folderName: String): File? {
            // Get the directory for the user's public Downloads directory.
            val folder = File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), folderName)
            return if (!folder.exists() && !folder.mkdirs()) {
                null
            } else folder
        }
        //endregion

        //region Load Default records
        fun loadDefaultAssets(activity: Activity) {

            // remove old recode first
            ObjectBoxHelper.shared().cleanUtilityBox()

            JsonUtility.loadUtilityFromFile("alectra.json", activity)
            var line = Exception().stackTrace[0].lineNumber + 1
            Timber.d("[$line] alectra")
            JsonUtility.loadUtilityFromFile("bell.json", activity)
            line = Exception().stackTrace[0].lineNumber + 1
            Timber.d("[$line] bell")
            JsonUtility.loadUtilityFromFile("enbridge.json", activity)
            line = Exception().stackTrace[0].lineNumber + 1
            Timber.d("[$line] enbridge")
            JsonUtility.loadUtilityFromFile("peel.json", activity)
            line = Exception().stackTrace[0].lineNumber + 1
            Timber.d("[$line] peel")
        }
    }
}
