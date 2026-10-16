package com.app.propertyfinder.utils

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object FileUtils {
    fun createProfilePicturePart(context: Context, uri: Uri): MultipartBody.Part? {
        val resolver = context.contentResolver
        val mime = resolver.getType(uri) ?: "image/jpeg"
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime) ?: "jpg"

        return try {
            val sourceFile = if (uri.scheme == "file") {
                val path = uri.path ?: return null
                File(path).takeIf { it.exists() } ?: return null
            } else {
                val tempFile = File(context.cacheDir, "profile_upload_${System.currentTimeMillis()}.$extension")
                resolver.openInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { output -> input.copyTo(output) }
                } ?: return null
                tempFile
            }

            val requestBody = sourceFile.asRequestBody(mime.toMediaTypeOrNull())
            MultipartBody.Part.createFormData("profilePicture", sourceFile.name, requestBody)
        } catch (_: Exception) {
            null
        }
    }
}
