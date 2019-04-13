package com.brightkey.nickfl.helpers

import android.content.Intent
import android.os.Bundle
import android.text.Editable

import org.json.JSONObject

import java.lang.ref.WeakReference
import java.util.HashMap

/**
 * @author Nick Floussov
 * @version 1.0.1
 * @since 1.0.0
 * Date: 1/24/2017
 */
object ValidationUtils {

    fun isNull(obj: Any?): Boolean {
        return obj == null
    }

    fun isStringEmpty(value: String): Boolean {
        val result = false

        return if (isNull(value) || value.isEmpty()) {
            true
        } else result

    }

    fun isCharSequenceEmpty(charSequence: CharSequence): Boolean {
        var result = false

        if (isNull(charSequence) || charSequence.length == 0) {
            result = true
        }

        return result
    }

    fun isTextFieldEmpty(editable: Editable): Boolean {
        return isNull(editable) || isStringEmpty(editable.toString())
    }

    fun isNumberNullOrZero(number: Number): Boolean {
        return isNull(number) || number.toInt() == 0
    }

    fun isListEmpty(list: List<*>): Boolean {
        return isNull(list) || list.size == 0
    }

    fun isArray(obj: Any): Boolean {
        if (!isNull(obj)) {
            if (obj.javaClass.isArray()) {
                return true
            }
        }

        return false
    }

    fun isArrayEmpty(obj: Array<Any>): Boolean {
        return if (!isNull(obj) && obj.size > 0) {
            false
        } else true
    }

    fun hasKey(jsonObject: JSONObject, key: String): Boolean {
        return !isStringEmpty(key) && !isNull(jsonObject) && jsonObject.has(key)
    }

    fun isMapEmpty(map: Map<*, *>): Boolean {
        return isNull(map) || map.size == 0
    }

    fun hasHashMapKey(hashMap: HashMap<*, *>, key: Any): Boolean {
        return !isMapEmpty(hashMap) && hashMap.containsKey(key)
    }

    fun isWeakReferenceNull(weakReference: WeakReference<*>): Boolean {
        return isNull(weakReference) || isNull(weakReference.get())
    }

    fun hasIntentExtras(intent: Intent): Boolean {
        return !isNull(intent) && !isNull(intent.extras) && intent.extras!!.size() > 0
    }

    fun isBundleEmpty(args: Bundle?): Boolean {
        return isNull(args) || args!!.size() == 0
    }

    fun isBundleAvailable(intent: Intent): Boolean {
        return !isNull(intent) && !isBundleEmpty(intent.extras)
    }

}
