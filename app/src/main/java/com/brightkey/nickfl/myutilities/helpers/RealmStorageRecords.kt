package com.brightkey.nickfl.myutilities.helpers

import android.content.Context
import android.os.Environment
import com.brightkey.nickfl.myutilities.entities.LoadUtility
import timber.log.Timber
import java.io.FileNotFoundException
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
        fun importRecords(): Boolean {

            // remove old recode first
            RealmHelper.shared().cleanAllUtilityBills()

            val folder = getPublicDownloadsStorageDir(Constants.folderRecordsName) ?: return false
            return readRecords(folder, Constants.fileRecordsName)
        }

        //read json file and store it in Realm records
        private fun readRecords(folder: File1, filename: String): Boolean {
            val file = File1(folder, filename)
            if (!file.exists()) {
                val line = Exception().stackTrace[0].lineNumber + 1
                Timber.i("[$line] file Not found: ${file.absolutePath}")
                return false
            }
            try {
                val fis = file.inputStream()
                LoadUtility.storeRecordsInRealm(fis)
            } catch (ex: FileNotFoundException) {
                val line = Exception().stackTrace[0].lineNumber + 1
                Timber.i("[$line] readRecords(error): $ex")
                return false
            } catch (ex: IOException) {
                val line = Exception().stackTrace[0].lineNumber + 1
                Timber.i("[$line] readRecords(error): $ex")
                return false
            }

            return true
        }
        //endregion

        //region Export records to Device from Realm
        fun exportRecords(): Boolean {

            val folder = getPublicDownloadsStorageDir(Constants.folderRecordsName) ?: return false
            val file = File1(folder, Constants.fileRecordsName)
            val list = RealmHelper.shared().fetchAllUtilityBills()
            val jsonContent = JsonUtility.convertToJson(list)
            try {
                val fos = file.outputStream()
                fos.use {
                    it.write(jsonContent.toByteArray())
                    it.close()
                }
            } catch (ex: IOException) {
                val line = Exception().stackTrace[0].lineNumber + 1
                Timber.i("[$line] exportRecords(error): $ex")
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
        fun loadDefaultAssets(context: Context) {

            // remove old recode first
            RealmHelper.shared().cleanAllUtilityBills()

            JsonUtility.loadUtilityFromFileToRealm<LoadUtility>("alectra.json", context)
            JsonUtility.loadUtilityFromFileToRealm<LoadUtility>("bell.json", context)
            JsonUtility.loadUtilityFromFileToRealm<LoadUtility>("enbridge.json", context)
            JsonUtility.loadUtilityFromFileToRealm<LoadUtility>("peel.json", context)
        }
    }
}