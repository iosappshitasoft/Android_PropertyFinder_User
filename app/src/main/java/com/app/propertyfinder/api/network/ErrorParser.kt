package com.app.propertyfinder.api.network


import org.json.JSONArray
import org.json.JSONObject

object ErrorParser {

    fun parse(raw: String?): String {
        if (raw.isNullOrBlank()) return "Unknown server error"

        return try {
            val json = JSONObject(raw)

            when {
                json.has("message") -> json.optString("message")
                json.has("error") -> json.optString("error")
                json.has("detail") -> json.optString("detail")
                json.has("errors") -> {
                    val errors = json.opt("errors")
                    when (errors) {
                        is JSONArray -> errors.toString()
                        is JSONObject -> errors.toString()
                        else -> errors?.toString() ?: raw
                    }
                }
                else -> raw
            }
        } catch (_: Exception) {
            raw
        }
    }
}