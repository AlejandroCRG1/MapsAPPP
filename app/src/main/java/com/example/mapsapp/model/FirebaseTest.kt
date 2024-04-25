package com.example.mapsapp.model

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseTest {
    private val database = FirebaseFirestore.getInstance()


    fun addMarcador(marcador: Marcador) {
        database.collection("marcadores")
            .add(
                hashMapOf(
                    "lat" to marcador.lat,
                    "lon" to marcador.lon,
                    "nom" to marcador.nom,
                    "desc" to marcador.desc,
                    "userId" to marcador.userId,
                    "type" to marcador.type,
                )
            )
    }

    fun editMarcador(editedMarcador: Marcador) {
        database.collection("marcadores").document(editedMarcador.id!!).set(
            hashMapOf(
                "lat" to editedMarcador.lat,
                "lon" to editedMarcador.lon,
                "nom" to editedMarcador.nom,
                "desc" to editedMarcador.desc,
                "userId" to editedMarcador.userId,
                "type" to editedMarcador.type,
            )
        )
    }

    fun deleteMarcador(id : String) {
        database.collection("marcadores").document(id).delete()
    }

    fun getMarcadores() : CollectionReference {
        return database.collection("marcadores")
    }



    fun getMarcador(id : String) : DocumentReference {
        return database.collection("marcadores").document(id)
    }
}