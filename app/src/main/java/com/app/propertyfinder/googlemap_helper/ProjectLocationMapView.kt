package com.app.propertyfinder.googlemap_helper

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.app.propertyfinder.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun ProjectLocationMapView(
    lat: Double,
    lng: Double,
    modifier: Modifier = Modifier,
    interactive: Boolean = false,
    showZoomControls: Boolean = false
) {
    val context = LocalContext.current
    val markerLocation = LatLng(lat, lng)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerLocation, 15f)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = showZoomControls,
            scrollGesturesEnabled = interactive,
            zoomGesturesEnabled = interactive,
            rotationGesturesEnabled = interactive,
            tiltGesturesEnabled = interactive
        )
    ) {
        Marker(
            state = MarkerState(position = markerLocation),
            title = "Project location",
            icon = bitmapDescriptorFromVector(context, R.drawable.ic_map_marker, 100, 130)
        )
    }
}
