package com.app.propertyfinder.helper

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.PrefUtils
import java.util.Locale

object LocaleManager {
    val TAG = LocaleManager::class.java.simpleName

    fun setLocale(c: Context): Context {
        val savedLanguage = getLanguageCode(c)
        return savedLanguage?.let { updateResources(c, it) } ?: c
    }

    fun getLanguageCode(c: Context): String? {
        return PrefUtils.getString(Constants.TAG_LANGUAGE_CODE, Constants.DEFAULT_LANGUAGE_CODE)
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        return LocaleManager.updateResources(
            context,
            locale
        )
    }

    @Suppress("DEPRECATION")
    private fun updateResources(context: Context, locale: Locale): Context {
        var mContext = context
        val res = mContext.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        mContext = mContext.createConfigurationContext(config)
        return mContext
    }

    fun getLocale(res: Resources): Locale {
        val config = res.configuration
        return if (Build.VERSION.SDK_INT >= 24) config.locales.get(0) else config.locale
    }

    fun isRTL(): Boolean {
        return Locale.getDefault().language == "ar"
    }
}