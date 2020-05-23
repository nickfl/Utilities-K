package com.brightkey.nickfl.myutilities.helpers

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/**
 * Created by paulfloussov on 2015-11-03.
 */
object PermissionHelper {

    // Storage Permissions
    private val PERMISSIONS_READ = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private val PERMISSIONS_WRITE = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    /**
     * Checks if the app has permission to use camera
     * If the app does not has permission then the user will be prompted to grant permissions
     * @param activity
     */
    fun requestReadPermissions(activity: Activity, requestCode: Int) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_READ,
                    requestCode
            )
        }
    }

    fun haveReadPermissions(activity: Activity): Boolean {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks if the app has permission to access fine location
     * If the app does not has permission then the user will be prompted to grant permissions
     * @param activity
     */
    fun requestWritePermissions(activity: Activity) {
        // Check if we have write permission
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_WRITE,
                    Constants.REQUEST_WRITE_PERMISSIONS
            )
        }
    }

    fun haveWritePermissions(activity: Activity): Boolean {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
}
