package com.app.propertyfinder.googlemap_helper

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.app.propertyfinder.api.dto.response.PropertyListItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager

@Composable
fun PropertyMapScreen(
    mapType: Int,
    items: List<PropertyListItem>,
    onMarkerClick: (PropertyListItem) -> Unit
) {

    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    var currentItems by remember { mutableStateOf(items) }
    currentItems = items
    var clusterManagerRef by remember { mutableStateOf<ClusterManager<PropertyClusterItem>?>(null) }
    var hasMovedToBounds by remember { mutableStateOf(false) }
    var lastItemsSignature by remember { mutableStateOf("") }
    val currentItemsSignature = remember(items) {
        items.mapNotNull { item ->
            val coords = item.location?.coordinates?.coordinates.orEmpty()
            val id = item.id
            if (id.isNullOrBlank() || coords.size < 2) null else "$id:${coords[0]}:${coords[1]}"
        }.sorted().joinToString("|")
    }

    AndroidView(
        factory = {
            mapView.apply {
                getMapAsync { googleMap ->
                    googleMap.mapType = mapType
                    val clusterManager = ClusterManager<PropertyClusterItem>(context, googleMap)

                    clusterManager.renderer = PropertyRenderer(context, googleMap, clusterManager)

                    googleMap.setOnCameraIdleListener(clusterManager)
                    googleMap.setOnMarkerClickListener(clusterManager)
                    clusterManager.setOnClusterItemClickListener { item ->
                        currentItems.firstOrNull { it.id == item.propertyId }?.let(onMarkerClick)
                        true
                    }
                    clusterManager.setOnClusterClickListener { cluster ->
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                cluster.position,
                                googleMap.cameraPosition.zoom + 2
                            )
                        )
                        true
                    }
                    clusterManagerRef = clusterManager
                    updateMapItems(
                        googleMap = googleMap,
                        clusterManager = clusterManager,
                        items = currentItems,
                        moveCameraToBounds = !hasMovedToBounds
                    )
                    if (currentItems.isNotEmpty()) {
                        hasMovedToBounds = true
                        lastItemsSignature = currentItemsSignature
                    }
                }
            }
        },
        update = { mapView ->
            mapView.getMapAsync { googleMap ->
                googleMap.mapType = mapType
                clusterManagerRef?.let { clusterManager ->
                    val shouldMoveToNewFilteredBounds = currentItemsSignature != lastItemsSignature
                    updateMapItems(
                        googleMap = googleMap,
                        clusterManager = clusterManager,
                        items = currentItems,
                        moveCameraToBounds = !hasMovedToBounds || shouldMoveToNewFilteredBounds
                    )
                    if (currentItems.isNotEmpty()) {
                        hasMovedToBounds = true
                        lastItemsSignature = currentItemsSignature
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

private fun updateMapItems(
    googleMap: com.google.android.gms.maps.GoogleMap,
    clusterManager: ClusterManager<PropertyClusterItem>,
    items: List<PropertyListItem>,
    moveCameraToBounds: Boolean
) {
    val validItems = items.mapNotNull { item ->
        val coords = item.location?.coordinates?.coordinates.orEmpty()
        if (item.id.isNullOrBlank() || coords.size < 2) {
            null
        } else {
            val lng = coords[0]
            val lat = coords[1]
            PropertyClusterItem(
                propertyId = item.id,
                lat = lat,
                lng = lng,
                titleText = item.title.orEmpty()
            )
        }
    }

    clusterManager.clearItems()
    clusterManager.addItems(validItems)
    clusterManager.cluster()

    if (moveCameraToBounds && validItems.isNotEmpty()) {
        val boundsBuilder = LatLngBounds.builder()
        validItems.forEach { boundsBuilder.include(LatLng(it.position.latitude, it.position.longitude)) }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 140))
    }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply { onCreate(null) }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    return mapView
}