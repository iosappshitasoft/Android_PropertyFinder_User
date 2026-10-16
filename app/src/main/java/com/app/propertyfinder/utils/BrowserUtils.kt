package com.app.propertyfinder.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/** CDN path or absolute URL → full media URL. */
fun resolveCdnOrAbsoluteMediaUrl(relativeOrAbsolute: String?, cdnBase: String): String? {
    val t = relativeOrAbsolute?.trim().orEmpty()
    if (t.isEmpty()) return null
    if (t.startsWith("http", ignoreCase = true) || t.startsWith("//")) return t
    val base = cdnBase.trimEnd('/')
    return "$base/${t.trimStart('/')}"
}

fun openUrlInChromeOrDefault(context: Context, url: String?, logTag: String = "OpenUrl") {
    val u = url?.trim().orEmpty()
    if (u.isEmpty()) return
    val uri = Uri.parse(u)
    val chromeIntent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.android.chrome")
    }
    try {
        context.startActivity(chromeIntent)
    } catch (_: Exception) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: Exception) {
            Log.e(logTag, "Could not open URL", e)
        }
    }
}
