package com.brightkey.nickfl.helpers

import android.content.Context
import android.content.SharedPreferences

import com.brightkey.nickfl.myutilities.MyUtilitiesApplication

/**
 * @author Nick Floussov
 * @version 1.0.1
 * @since 1.0.0
 * Date: 12/25/2016
 */
object SharedPreferencesHelper {

    //region Preference Keys
    val LAST_DB_SCHEMA_VERSION = "prefs_last_db_schema_version"

    // My Profile Keys
    val MY_USER_NAME = "prefs_my_user_name"
    val MY_USER_AVATAR = "prefs_my_user_local_image_name"
    val MY_TOKEN_ACCESS_VALUE = "prefs_my_token_access_value"
    val MY_TOKEN_EXPIRATION = "prefs_my_token_expiration_date"
    //endregion

    private val PREFS_TAG = "shared_preferences_myutilities_app"

    private val sharedPreferences: SharedPreferences
        get() {
            val context = MyUtilitiesApplication.context
            return context!!.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE)
        }

    fun getPrefForStringValue(prefName: String): String? {
        val prefs = sharedPreferences
        return prefs.getString(prefName, "")
    }

    fun changePrefForStringValue(prefName: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(prefName, value)
        editor.apply()
    }

    fun getPrefForIntValue(prefName: String): Int {
        val prefs = sharedPreferences
        return prefs.getInt(prefName, -1)
    }

    fun changePrefForIntValue(prefName: String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(prefName, value)
        editor.apply()
    }

    fun changePrefForLongValue(prefName: String, value: Long) {
        val editor = sharedPreferences.edit()
        editor.putLong(prefName, value)
        editor.apply()
    }

    /*
    public static Date getPrefForDateValue(String prefName) {
        SharedPreferences prefs = getSharedPreferences();
        long dateLong = prefs.getLong(prefName, -1L);
        return new Date(dateLong);
    }

    public static boolean isPrefDateExpired() {
        SharedPreferences prefs = getSharedPreferences();
        long dateLong = prefs.getLong(MY_TOKEN_EXPIRATION, -1L);
        if (dateLong < 0L) {
            return true;
        }
        Date date = new Date(dateLong);
        Date now = new Date();
        return now.after(date); // expired!
    }

    //store date as long
    public static void changePrefForDateValue(String prefName, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        long valueLong = utcStringToLongDate(value);
        editor.putLong(prefName, valueLong);
        editor.apply();
    }

    public static int getMyAvatarResource() {
        String avatar = SharedPreferencesHelper.getPrefForStringValue(MY_USER_AVATAR).toLowerCase();
        return getAvatarResourceIdByName(avatar);
    }

    private static int getAvatarResourceIdByName(String avatarName) {
        if (avatarName != null && !(avatarName.isEmpty())) {
            if (avatarName.contains("seaweed")) {
                return R.drawable.char_seaweed_99;
            } else
            if (avatarName.contains("wave")) {
                return R.drawable.char_wave_99;
            }
        }
        return R.drawable.char_rock_99;
    }

    public static void setMyAvatarResource(int resId) {
        String newAvatar = getAvatarNameByResourceId(resId);
        SharedPreferencesHelper.changePrefForStringValue(MY_USER_AVATAR, newAvatar);
    }

    private static String getAvatarNameByResourceId(int resId) {
        if (resId == R.drawable.char_seaweed_99) {
            return "char_seaweed_99";
        }
        if (resId == R.drawable.char_wave_99) {
            return "char_wave_99";
        }
        return "char_rock_99";
    }
*/
}
