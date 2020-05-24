package com.brightkey.nickfl.myutilities.helpers

import android.app.Activity
import android.os.Environment
import com.brightkey.nickfl.myutilities.entities.LoadUtility
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.File as File1

class RealmStorageRecords {

    /* Checks if external storage is available for read and write */
    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    /* Checks if external storage is available to at least read */
    private val isExternalStorageReadable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
        }

    companion object {

        //region Import records from Device to Realm
        fun importRecords(activity: Activity): Boolean {

            // remove old recode first
            RealmHelper.shared().cleanAllUtilityBills()

            val folder = getPublicDownloadsStorageDir(Constants.folderRecordsName) ?: return false
            return readRecords(folder, Constants.fileRecordsName)
        }

        //read json file and store it in Realm records
        private fun readRecords(folder: File1, filename: String): Boolean {
            val file = File1(folder, filename)
            if (!file.exists()) {
                return false
            }
            try {
                val fis = FileInputStream(file)
                LoadUtility.storeRecordsInRealm(fis)
            } catch (ex: FileNotFoundException) {
                return false
            } catch (ex: IOException) {
                return false
            }

            return true
        }
        //endregion

        //region Export records to Device from Realm
        fun exportRecords(activity: Activity): Boolean {

            val folder = getPublicDownloadsStorageDir(Constants.folderRecordsName) ?: return false
            val file = File1(folder, Constants.fileRecordsName)
            val list = RealmHelper.shared().fetchAllUtilityBills()
            val jsonContent = JsonUtility.convertToJson(list)
            try {
                val out = FileOutputStream(file)
                out.write(jsonContent.toByteArray())
                out.close()
            } catch (ex: IOException) {
                return false
            }

            return true
        }

        private fun getPublicDownloadsStorageDir(folderName: String): File1? {
            // Get the directory for the user's public Downloads directory.
            val folder = File1(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), folderName)
            return if (!folder.exists() && !folder.mkdirs()) {
                null
            } else folder
        }
        //endregion

        //region Load Default records
        fun loadDefaultAssets(activity: Activity) {

            // remove old recode first
            RealmHelper.shared().cleanAllUtilityBills()

            JsonUtility.loadUtilityFromFileToRealm("alectra.json", activity)
            JsonUtility.loadUtilityFromFileToRealm("bell.json", activity)
            JsonUtility.loadUtilityFromFileToRealm("enbridge.json", activity)
            JsonUtility.loadUtilityFromFileToRealm("peel.json", activity)
        }
    }
}