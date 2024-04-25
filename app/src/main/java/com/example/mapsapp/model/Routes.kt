package com.example.mapsapp.model

sealed class Routes(val route:String) {
    data object Pantalla0: Routes("pantalla0")
    data object Pantalla1: Routes("pantalla1")
    data object Pantalla2: Routes("pantalla2")
}