package com.example.mapsapp.viewModel

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.mapsapp.model.Categoria
import com.example.mapsapp.model.FirebaseTest
import com.example.mapsapp.model.Marcador
import com.example.mapsapp.model.Routes
import com.example.mapsapp.model.UserPrefs
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.DocumentChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class viewModel : ViewModel() {

    var databaseConnection = FirebaseTest()
    var auth = FirebaseAuth.getInstance()

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

    var show = MutableStateFlow(false)

    fun changeShow() {
        show.value = !show.value
    }

    fun getEmail() : String {
        return email.value
    }

    fun getPass() : String {
        return password.value
    }

    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        confirmPassword.value = newConfirmPassword
    }

    val goToNext = MutableLiveData(false)
    val _goToNext = goToNext

    val userId = MutableLiveData("")
    val _userId = userId

    val loggedUser = MutableLiveData("")
    val _loggedUser = loggedUser

    fun observeGoToNext(lifecycleOwner: LifecycleOwner, navController : NavController) {
        _goToNext.observe(lifecycleOwner) { goToNext ->
            if (goToNext) {
                navController.navigate(Routes.Pantalla2.route)
            }
        }
    }

    var error = ""

    fun changeError(text : String) {
        error = text
    }

    fun register(auth : FirebaseAuth, username : String, password : String, userPrefs: UserPrefs) {
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _goToNext.value = true
                    CoroutineScope(Dispatchers.IO).launch {
                        userPrefs.saveUserData(username, password)
                    }
                }
                else {
                    _goToNext.value = false
                    val exception = task.exception
                    if (exception is FirebaseAuthException) {
                        error = exception.message.toString()
                        changeShow()
                    }
                }
            }
    }

    fun login(auth : FirebaseAuth, username : String?, password : String?, userPrefs: UserPrefs) {
        auth.signInWithEmailAndPassword(username!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userId.value = task.result.user?.uid
                    _loggedUser.value = task.result.user?.email?.split("@")?.get(0)
                    _goToNext.value = true
                    CoroutineScope(Dispatchers.IO).launch {
                        userPrefs.saveUserData(username, password)
                    }
                }
                else {
                    _goToNext.value = false
                    val exception = task.exception
                    if (exception is FirebaseAuthException) {
                        error = exception.message.toString()
                        changeShow()
                    }
                }
            }
    }

    fun logout(auth: FirebaseAuth, userPrefs: UserPrefs) {
        auth.signOut()
        CoroutineScope(Dispatchers.IO).launch {
            userPrefs.delUserData()
        }
    }


    var showAddMarker = MutableStateFlow(false)
    var showEditMarker = MutableStateFlow(false)

    var markedPosition = MutableLiveData<LatLng>()

    fun editMarkedPosition(newValue: LatLng){
        markedPosition.value = newValue
    }

    fun getMarkedPosition() : LatLng {
        return markedPosition.value!!
    }

    var marcadores = MutableLiveData<MutableList<Marcador>>()

    fun getAllMarkers() {
        databaseConnection.getMarcadores()
            .whereEqualTo("userId", auth.uid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return@addSnapshotListener
                }
                val tempList = mutableListOf<Marcador>()
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val newMarker = dc.document.toObject(Marcador::class.java)
                        newMarker.id = dc.document.id
                        newMarker.lat =
                            dc.document.get("lat").toString().toDouble()
                        newMarker.lon =
                            dc.document.get("lon").toString().toDouble()
                        newMarker.type = dc.document.get("type").toString()
                        newMarker.userId = dc.document.get("userId").toString()
                        newMarker.desc = dc.document.get("desc").toString()
                        newMarker.nom = dc.document.get("nom").toString()
                        tempList.add(newMarker)
                    }

                }
                marcadores.value = tempList
            }
    }

    var deviceLatLng = MutableStateFlow(LatLng(0.0, 0.0))

    fun setLatLng(value : LatLng) {
        deviceLatLng.value = value
    }

    fun changeShowAddMarker() {
        showAddMarker.value = !showAddMarker.value
    }

    fun changeShowEditMarker() {
        showEditMarker.value = !showEditMarker.value
    }

    var categories = mutableListOf(
        Categoria("Business", Icons.Filled.Business),
        Categoria("Houses", Icons.Filled.Home,),
        Categoria("Fighting Gym", Icons.Filled.SportsMartialArts),
        Categoria("Another", Icons.Filled.LocationOn)
    )



    var idMarc = MutableStateFlow("")
    var nomMarc = MutableStateFlow("")
    var descMarc = MutableStateFlow("")
    var categoriaMarc = MutableStateFlow("Seleccione categoria")

    fun getIdMarc() : String {
        return idMarc.value
    }

    fun setIdMarc(txt : String) {
        idMarc.value = txt
    }

    fun getNom() : String {
        return nomMarc.value
    }

    fun setNomMarc(txt : String) {
        nomMarc.value = txt
    }

    fun getDescMarc() : String {
        return descMarc.value
    }

    fun setDescMarc(txt : String) {
        descMarc.value = txt
    }

    fun getCategoriaMarc() : String {
        return categoriaMarc.value
    }

    fun setCategoriaMarc(txt : String) {
        categoriaMarc.value = txt
    }

}