package com.example.mapsapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mapsapp.model.Marcador
import com.example.mapsapp.model.Routes
import com.example.mapsapp.model.UserPrefs
import com.example.mapsapp.viewModel.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun MapScreen(navController: NavHostController, viewModel: viewModel, userPrefs: UserPrefs) {
    val permissionState = rememberPermissionState(permission = android.Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }
    if (permissionState.status.isGranted) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val items = listOf(
                TabItem("Map", Icons.Outlined.Map),
                TabItem("Markers", Icons.Outlined.Favorite)
            )
            var selectedItem by remember { mutableStateOf(0) }
            val pagerState = rememberPagerState {
                items.size
            }

            LaunchedEffect(selectedItem) {
                pagerState.animateScrollToPage(selectedItem)
            }
            LaunchedEffect(pagerState.currentPage) {
                selectedItem = pagerState.currentPage
            }
            var show = remember { mutableStateOf(true) }
            val scope = rememberCoroutineScope()
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 13.dp)) {
                    IconButton(onClick = {
                        viewModel.logout(viewModel.auth, userPrefs)
                        navController.navigate(Routes.Pantalla0.route)
                    } ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "")
                    }
                }
                TabRow(selectedTabIndex = selectedItem) {
                    items.forEachIndexed { index, item ->
                        Tab(
                            selected = index == selectedItem,
                            onClick = { selectedItem = index },
                            text = { Text(text = item.title) },
                            icon = { Icon(imageVector = item.icon, contentDescription = null) }
                        )
                    }
                }
            }


            HorizontalPager(state = pagerState) { index ->
                if (index == 0) {
                    val scope = rememberCoroutineScope()
                    val sheetState = rememberModalBottomSheetState()
                    var showEdit = viewModel.showEditMarker.collectAsState()
                    ShowMap(viewModel = viewModel)
                    var showBottomSheet = viewModel.showAddMarker.collectAsState()
                    if (showBottomSheet.value) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                viewModel.changeShowAddMarker()
                            },
                            sheetState = sheetState
                        ) {
                            AddMarkerScreen(
                                viewModel = viewModel,
                                onCloseBottomSheet = {
                                    scope.launch { sheetState.hide() }
                                        .invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                viewModel.changeShowAddMarker()
                                            }
                                        }
                                }
                            )

                        }
                    }
                    if (showEdit.value) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                viewModel.changeShowEditMarker()
                            },
                            sheetState = sheetState
                        ) {
                            EditMarkerScreen(
                                viewModel = viewModel,
                                onCloseBottomSheet = {
                                    scope.launch { sheetState.hide() }
                                        .invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                viewModel.changeShowEditMarker()
                                            }
                                        }
                                }
                            )
                        }
                    }
                }
                else {
                    var marcadores = viewModel.marcadores.observeAsState()
                    var categorias = viewModel.categories
                    var business = remember {
                        mutableStateOf(true)
                    }
                    var house = remember {
                        mutableStateOf(true)
                    }
                    var martial = remember {
                        mutableStateOf(true)
                    }
                    var another = remember {
                        mutableStateOf(true)
                    }
                    var allowed = remember { mutableStateListOf<String>("Business", "Houses", "Fighting Gym", "Another") }
                    viewModel.getAllMarkers()
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top
                            ) {
                                Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                                    Row(horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = business.value,
                                            onCheckedChange = {
                                                business.value = it // Update directly
                                                if (business.value == true) allowed.add("Business")
                                                else allowed.remove("Business")
                                            }
                                        )
                                        Icon(
                                            imageVector = Icons.Filled.Business,
                                            contentDescription = ""
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = house.value,
                                            onCheckedChange = {
                                                house.value = it
                                                if (house.value == true) allowed.add("Houses")
                                                else allowed.remove("Houses")
                                            }
                                        )
                                        Icon(
                                            imageVector = Icons.Filled.House,
                                            contentDescription = ""
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = martial.value,
                                            onCheckedChange = {
                                                martial.value = it
                                                if (martial.value == true) allowed.add("Fighting Gym")
                                                else allowed.remove("Fighting Gym")
                                            }
                                        )
                                        Icon(
                                            imageVector = Icons.Filled.SportsMartialArts,
                                            contentDescription = ""
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = another.value,
                                            onCheckedChange = {
                                                another.value = it
                                                if (another.value == true) allowed.add("Another")
                                                else allowed.remove("Another")
                                            }
                                        )
                                        Icon(
                                            imageVector = Icons.Filled.LocationOn,
                                            contentDescription = ""
                                        )
                                    }
                                }

                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    items(marcadores.value!!.size) {
                                        var icon = Icons.Filled.AddLocation
                                        for (i in categorias)
                                            if (i.nom == marcadores.value!![it].type)
                                                icon = i.icon
                                        if (marcadores.value!![it].type in allowed) {
                                            ListItem(
                                                headlineContent = { Text(marcadores.value!![it].nom) },
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clickable {
                                                        viewModel.changeShowEditMarker()
                                                        viewModel.editMarkedPosition(
                                                            LatLng(
                                                                marcadores.value!![it].lat,
                                                                marcadores.value!![it].lon
                                                            )
                                                        )
                                                        viewModel.setNomMarc(marcadores.value!![it].nom)
                                                        viewModel.setDescMarc(marcadores.value!![it].desc)
                                                        viewModel.setCategoriaMarc(marcadores.value!![it].type)
                                                        viewModel.setIdMarc(marcadores.value!![it].id!!)
                                                    },
                                                supportingContent = {
                                                    Text(text = marcadores.value!![it].desc)
                                                },
                                                trailingContent = {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = ""
                                                    )
                                                }
                                            )
                                            Divider()
                                        }
                                    }
                                }
                            }
                        }
                        ExtendedFloatingActionButton(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(bottom = 30.dp, start = 4.dp),
                            onClick = {
                                viewModel.changeShowAddMarker()
                                viewModel.editMarkedPosition(viewModel.deviceLatLng.value)
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = ""
                                )
                            },
                            text = { Text(text = "Add marker") })
                    }
                    val scope = rememberCoroutineScope()
                    val sheetState = rememberModalBottomSheetState()
                    var showEdit = viewModel.showEditMarker.collectAsState()
                    ShowMap(viewModel = viewModel)
                    var showBottomSheet = viewModel.showAddMarker.collectAsState()
                    if (showBottomSheet.value) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                viewModel.changeShowAddMarker()
                            },
                            sheetState = sheetState
                        ) {
                            AddMarkerScreen(
                                viewModel = viewModel,
                                onCloseBottomSheet = {
                                    scope.launch { sheetState.hide() }
                                        .invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                viewModel.changeShowAddMarker()
                                            }
                                        }
                                }
                            )

                        }
                    }
                    if (showEdit.value) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                viewModel.changeShowEditMarker()
                            },
                            sheetState = sheetState
                        ) {
                            EditMarkerScreen(
                                viewModel = viewModel,
                                onCloseBottomSheet = {
                                    scope.launch { sheetState.hide() }
                                        .invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                viewModel.changeShowEditMarker()
                                            }
                                        }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    else {
        Text("Need permission!")
    }

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddMarkerScreen(
    viewModel: viewModel,
    onCloseBottomSheet: () -> Unit
) {
    var expandedCat by remember {
        mutableStateOf(false)
    }
    val categorias = viewModel.categories


    var nomMarc = viewModel.nomMarc.collectAsState()
    var descMarc = viewModel.descMarc.collectAsState()
    var categoriaMarc = viewModel.categoriaMarc.collectAsState("Seleccione categoria")

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = nomMarc.value,
            onValueChange = { viewModel.setNomMarc(it) },
            label = { Text("Nom") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = descMarc.value,
            onValueChange = { viewModel.setDescMarc(it) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = categoriaMarc.value,
                onValueChange = { /* No permitimos cambios directos aquí */ },
                enabled = false,
                readOnly = true,
                modifier = Modifier
                    .clickable { expandedCat = true }
                    .fillMaxWidth()
            )

            DropdownMenu(
                expanded = expandedCat,
                onDismissRequest = { expandedCat = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                categorias.forEach { categoria ->
                    DropdownMenuItem(text = { Text(text = categoria.nom) }, onClick = {
                        expandedCat = false
                        viewModel.setCategoriaMarc(categoria.nom)
                    })
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        var show by remember { mutableStateOf(false) }
        FloatingActionButton(
            onClick = {
                if (categoriaMarc.value == "Seleccione categoria" || viewModel.getNom() == "") {
                    show = true
                } else {
                    val latLng = viewModel.getMarkedPosition()
                    val markerToAdd =
                            Marcador(
                                "",
                                latLng.latitude,
                                latLng.longitude,
                                nomMarc.value,
                                descMarc.value,
                                viewModel.auth.uid!!,
                                categoriaMarc.value
                            )
                    viewModel.databaseConnection.addMarcador(markerToAdd)
                    onCloseBottomSheet()
                    viewModel.setCategoriaMarc("Seleccionar Categoría")
                    viewModel.setDescMarc("")
                    viewModel.setNomMarc("")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Marker")
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditMarkerScreen(
    viewModel: viewModel,
    onCloseBottomSheet: () -> Unit
) {
    var expandedCat by remember {
        mutableStateOf(false)
    }
    val categorias = viewModel.categories

    var id = viewModel.idMarc.collectAsState()
    var nomMarc = viewModel.nomMarc.collectAsState()
    var descMarc = viewModel.descMarc.collectAsState()
    var categoriaMarc = viewModel.categoriaMarc.collectAsState()


    Column(
        modifier = Modifier
            .padding(16.dp)
            .padding(bottom = 30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = nomMarc.value,
            onValueChange = { viewModel.setNomMarc(it) },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = descMarc.value,
            onValueChange = { viewModel.setDescMarc(it) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = categoriaMarc.value,
                onValueChange = { /* No permitimos cambios directos aquí */ },
                enabled = false,
                readOnly = true,
                modifier = Modifier
                    .clickable { expandedCat = true }
                    .fillMaxWidth()
            )

            DropdownMenu(
                expanded = expandedCat,
                onDismissRequest = { expandedCat = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                categorias.forEach { categoria ->
                    DropdownMenuItem(text = { Text(text = categoria.nom) }, onClick = {
                        expandedCat = false
                        viewModel.setCategoriaMarc(categoria.nom)
                    })
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        var show by remember { mutableStateOf(false) }
            Button(
                onClick = {
                    if (categoriaMarc.value == "Seleccione categoria" || viewModel.getNom() == "") {
                        show = true
                    } else {
                        val latLng = viewModel.getMarkedPosition()
                        val markerToEdit =
                            Marcador(
                                id.value,
                                latLng.latitude,
                                latLng.longitude,
                                nomMarc.value,
                                descMarc.value,
                                viewModel.auth.uid!!,
                                categoriaMarc.value
                            )
                        viewModel.databaseConnection.editMarcador(markerToEdit)
                        onCloseBottomSheet()
                        viewModel.setCategoriaMarc("Seleccionar Categoría")
                        viewModel.setDescMarc("")
                        viewModel.setNomMarc("")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Marker")
            }
            FilledTonalButton(
                onClick = {
                    viewModel.databaseConnection.deleteMarcador(id.value)
                    onCloseBottomSheet()
                    viewModel.setCategoriaMarc("Seleccionar Categoría")
                    viewModel.setDescMarc("")
                    viewModel.setNomMarc("")
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Delete Marker")
            }
        }
}
