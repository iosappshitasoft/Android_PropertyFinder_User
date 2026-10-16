package com.app.propertyfinder.utils

import android.content.Context
import android.content.SharedPreferences

object PrefUtils {
    private val TAG: String = PrefUtils::class.java.simpleName
    private lateinit var preferences: SharedPreferences
    private lateinit var adminPreferences: SharedPreferences
    private lateinit var themePref: SharedPreferences
    const val ADMIN_PREFERENCE_NAME: String = "ADMIN_PREF"
    const val PREFERENCE_NAME: String = "DELIVERY_PREF"
    const val THEME_PREF_NAME = "THEME_PREF"
    const val PRIVATE_MODE = Context.MODE_PRIVATE

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFERENCE_NAME, PRIVATE_MODE)
        adminPreferences = context.getSharedPreferences(ADMIN_PREFERENCE_NAME, PRIVATE_MODE)
        themePref = context.getSharedPreferences(THEME_PREF_NAME, PRIVATE_MODE)
    }

    fun save(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun save(key: String, value: Float) {
        preferences.edit().putFloat(key, value).apply()
    }

    fun save(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun save(key: String, value: Long) {
        preferences.edit().putLong(key, value).apply()
    }

    fun save(key: String, value: Double) {
        preferences.edit().putString(key, value.toString()).apply()
    }

    fun save(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun save(key: String, value: Set<String>) {
        preferences.edit().putStringSet(key, value).apply()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    fun getFloat(key: String, defValue: Float): Float {
        return try {
            preferences.getFloat(key, defValue)
        } catch (ex: ClassCastException) {
            preferences.getString(key, defValue.toString())!!.toFloat()
        }
    }

    fun getDouble(key: String, defValue: Double): Double {
        if (!preferences.contains(key)) return defValue
        else return getString(key, "" + 0)!!.toDouble()
    }

    fun getInt(key: String, defValue: Int): Int {
        return try {
            preferences.getInt(key, defValue)
        } catch (ex: ClassCastException) {
            preferences.getString(key, defValue.toString())!!.toInt()
        }
    }

    fun getLong(key: String, defValue: Long): Long {
        return try {
            preferences.getLong(key, defValue)
        } catch (ex: ClassCastException) {
            preferences.getString(key, defValue.toString())!!.toLong()
        }
    }

    public fun getString(key: String, defValue: String): String? {
        return try {
            preferences.getString(key, defValue)
        } catch (_: ClassCastException) {
            // Migration safety: old builds stored TAG_LOGIN as Boolean.
            val legacyValue = preferences.all[key]
            if (key == Constants.TAG_LOGIN && legacyValue is Boolean) {
                val migrated = if (legacyValue) Constants.LOGIN_USER else ""
                preferences.edit().remove(key).putString(key, migrated).apply()
                migrated
            } else {
                legacyValue?.toString() ?: defValue
            }
        }
    }

    fun saveAdminPref(key: String, value: Int) {
        adminPreferences.edit().putInt(key, value).apply()
    }

    fun saveAdminPref(key: String, value: Float) {
        adminPreferences.edit().putFloat(key, value).apply()
    }

    fun saveAdminPref(key: String, value: Double) {
        adminPreferences.edit().putLong(key, value.toBits()).apply()
    }

    fun saveAdminPref(key: String, value: String) {
        adminPreferences.edit().putString(key, value).apply()
    }

    fun getAdminPref(key: String, defValue: Int): Int {
        return adminPreferences.getInt(key, defValue)
    }

    fun getAdminPref(key: String, defValue: Float): Float {
        return adminPreferences.getFloat(key, defValue)
    }

    fun getAdminPref(key: String, defValue: Double): Double {
        return Double.fromBits(adminPreferences.getLong(key, defValue.toBits()))
    }

    fun getAdminPref(key: String, defValue: String): String {
        return adminPreferences.getString(key, defValue).toString()
    }

    fun saveThemePref(key: String, value: Int) {
        themePref.edit().putInt(key, value).apply()
    }

    fun saveThemePref(key: String, value: String) {
        themePref.edit().putString(key, value).apply()
    }

    fun getThemePref(key: String, defValue: Int): Int {
        return themePref.getInt(key, defValue)
    }

    fun getThemePref(key: String, defValue: String): String {
        return themePref.getString(key, defValue).toString()
    }

    fun remove(key: String) {
        preferences.edit().remove(key).commit()
    }

    fun clear() {
        preferences.edit().clear().commit()
    }
}