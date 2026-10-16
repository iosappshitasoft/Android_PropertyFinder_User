package com.app.propertyfinder.utils

import android.text.format.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private fun parseIso8601UtcToEpochMillis(iso: String): Long? {
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSX",
        "yyyy-MM-dd'T'HH:mm:ss'Z'"
    )
    for (pattern in patterns) {
        try {
            val sdf = SimpleDateFormat(pattern, Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.parse(iso)?.time
        } catch (_: ParseException) {
            // try next pattern
        }
    }
    return null
}

/** Relative label for [publishedAt] (e.g. "1 day ago"); em dash when missing or unparseable. */
fun formatListedTiming(publishedAt: String?): String {
    val ms = publishedAt?.let { parseIso8601UtcToEpochMillis(it) } ?: return "—"
    val now = System.currentTimeMillis()
    return DateUtils.getRelativeTimeSpanString(
        ms,
        now,
        DateUtils.MINUTE_IN_MILLIS
    ).toString()
}
