package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class ReportUploadAttachmentsResponse(
    @SerializedName("baseUrl")
    val baseUrl: String? = null,
    @SerializedName("uploads")
    val uploads: ReportUploadItems? = null,
    @SerializedName("baseUrls")
    val baseUrls: ReportUploadBaseUrls? = null
)

data class ReportUploadItems(
    @SerializedName("images")
    val images: List<ReportUploadItem> = emptyList(),
    @SerializedName("videos")
    val videos: List<ReportUploadItem> = emptyList(),
    @SerializedName("documents")
    val documents: List<ReportUploadItem> = emptyList()
)

data class ReportUploadBaseUrls(
    @SerializedName("images")
    val images: String? = null,
    @SerializedName("videos")
    val videos: String? = null,
    @SerializedName("documents")
    val documents: String? = null
)

data class ReportUploadItem(
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("path")
    val path: String? = null,
    @SerializedName("filename")
    val filename: String? = null,
    @SerializedName("size")
    val size: Long? = null,
    @SerializedName("mimetype")
    val mimetype: String? = null
)

