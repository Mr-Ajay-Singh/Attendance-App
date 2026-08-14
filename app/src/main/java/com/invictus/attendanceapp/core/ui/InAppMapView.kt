package com.invictus.attendanceapp.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

@Composable
fun InAppMapView(
    latitude: Double,
    longitude: Double,
    label: String = "Attendance Location",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapView(context).apply {
            getMapAsync { maplibreMap ->
                val targetLocation = LatLng(latitude, longitude)

                // Clean UI Settings
                maplibreMap.uiSettings.isLogoEnabled = false
                maplibreMap.uiSettings.isAttributionEnabled = false

                // High-detail street-level vector map style with roads, buildings & street labels
                val streetMapStyle = "https://basemaps.cartocdn.com/gl/voyager-gl-style/style.json"

                maplibreMap.setStyle(
                    Style.Builder().fromUri(streetMapStyle)
                ) {
                    maplibreMap.cameraPosition = CameraPosition.Builder()
                        .target(targetLocation)
                        .zoom(15.0)
                        .build()

                    maplibreMap.addMarker(
                        MarkerOptions()
                            .position(targetLocation)
                            .title(label)
                    )
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
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
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Surface(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { mapView }
            )
        }
    }
}
