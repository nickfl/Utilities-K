package com.brightkey.nickfl.myutilities

import android.app.Application
import android.content.Context
import com.brightkey.nickfl.myutilities.entities.ConfigEntity
import com.brightkey.nickfl.myutilities.helpers.JsonUtility
import io.realm.Realm
import timber.log.Timber

/**
 * @author Nick Floussov
 * @version 1.0.1
 * @since 1.0.0
 * Date: 2/09/2017
 */
class MyUtilitiesApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        Realm.init(this)

        //start Logger
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        config = JsonUtility.loadConfigFromAsset("configuration.json", this)
        config?.let {
            val line = Exception().stackTrace[0].lineNumber + 1
            Timber.d("[$line] config: $it.toString()")
        }
    }

    // Overriding this method is totally optional!
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//    }

    // Overriding this method is totally optional!
//    override fun onLowMemory() {
//        super.onLowMemory()
//    }

    companion object {

        /**
         * Returns the application context
         * @return application context
         */
        lateinit var context: Context
            private set
        var config: List<ConfigEntity>? = null
            private set

        fun getConfigEntityForType(type: String): ConfigEntity? {
            for (item in config!!) {
                if (item.utilityIcon == type) {
                    return item
                }
            }
            return null
        }
    }
}
