package com.app.propertyfinder.googlemap_helper

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class PropertyClusterItem(
    val propertyId: String,
    private val lat: Double,
    private val lng: Double,
    private val titleText: String
) : ClusterItem {

    override fun getPosition(): LatLng {
        return LatLng(lat, lng)
    }

    override fun getTitle(): String {
        return titleText
    }

    override fun getSnippet(): String {
        return ""
    }

    override fun getZIndex(): Float? {
        return null // or return 0f if you want default stacking
    }
}