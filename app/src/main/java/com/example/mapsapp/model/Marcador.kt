package com.example.mapsapp.model

data class Marcador(
    var id : String?,
    var lat : Double,
    var lon : Double,
    var nom : String,
    var desc : String,
    var userId : String,
    var type : String
) {
    constructor() : this( "", 0.0, 0.0, "", "", "", "")
}
