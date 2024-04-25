package com.example.mapsapp.view

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.model.Routes
import com.example.mapsapp.model.UserPrefs
import com.example.mapsapp.ui.theme.MapsAppTheme
import com.example.mapsapp.viewModel.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var viewModel = viewModel()
            val navController = rememberNavController()
            viewModel.observeGoToNext(this, navController)
            MapsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val userPrefs = UserPrefs(context)
                    NavHost(
                        navController = navController,
                        startDestination = Routes.Pantalla0.route
                    ) {
                        composable(Routes.Pantalla0.route) { SignInScreen(navController, viewModel, userPrefs) }
                        composable(Routes.Pantalla1.route) { SignUpScreen(navController, viewModel) }
                        composable(Routes.Pantalla2.route) { MapScreen(navController, viewModel, userPrefs) }
                    }
                }
            }
        }
    }
}

data class TabItem (
    val title : String,
    val icon : ImageVector
)



@SuppressLint("MissingPermission")
@Composable
fun ShowMap(viewModel: viewModel) {
    val context = LocalContext.current
    val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context)}
    var lastKnownLocation by remember { mutableStateOf<Location?>(null)}
    var deviceLatLng by remember { mutableStateOf(LatLng(0.0, 0.0))}
    val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)}
    val locationResult = fusedLocationProviderClient.getCurrentLocation(100, null)
    var marcadores = viewModel.marcadores.observeAsState()
    viewModel.getAllMarkers()

    locationResult.addOnCompleteListener(context as MainActivity) { task ->
        if (task.isSuccessful) {
            lastKnownLocation = task.result
            deviceLatLng = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
            viewModel.setLatLng(deviceLatLng)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
        } else {
            Log.e("Error", "Exception: %s", task.exception)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = true,
                mapType = MapType.HYBRID,
                isTrafficEnabled = true
            ),
            onMapLongClick = {
                viewModel.editMarkedPosition(it)
                viewModel.changeShowAddMarker()
            },
        ) {
            marcadores.value?.forEach { marker ->
                Marker(
                    state = MarkerState(
                        LatLng(
                            marker.lat,
                            marker.lon
                        )
                    ),
                    title = marker.nom,
                    snippet = marker.desc,
                    icon = BitmapDescriptorFactory.defaultMarker(
                        when (marker.type) {
                            "Houses" -> BitmapDescriptorFactory.HUE_CYAN
                            "Business" -> BitmapDescriptorFactory.HUE_YELLOW
                            "Another" -> BitmapDescriptorFactory.HUE_GREEN
                            else -> BitmapDescriptorFactory.HUE_RED
                        }),
                    onClick = {
                        viewModel.changeShowEditMarker()
                        viewModel.editMarkedPosition(LatLng(
                            marker.lat,
                            marker.lon
                        ))
                        viewModel.setNomMarc(marker.nom)
                        viewModel.setDescMarc(marker.desc)
                        viewModel.setCategoriaMarc(marker.type)
                        viewModel.setIdMarc(marker.id!!)
                        return@Marker false
                    }
                )
            }
        }

        ExtendedFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 30.dp, start = 4.dp),
            onClick = {
                viewModel.changeShowAddMarker()
                viewModel.editMarkedPosition(deviceLatLng)
                      },
            icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = "") },
            text = { Text(text = "Add marker") })
    }
}
