package com.example.culinar.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinar.DataStore.DataStoreManager
import com.example.culinar.models.viewModels.USER_FIREBASE_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

const val USER_FIREBASE_COLLECTION = "users"

class SessionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Firebase.firestore
    private val dataStore = DataStoreManager(application)

    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username.asStateFlow()

    private val _id = MutableStateFlow<String?>(null)
    val id: StateFlow<String?> = _id.asStateFlow()

    private val _isExpert = MutableStateFlow<Boolean?>(null)
    val isExpert: StateFlow<Boolean?> = _isExpert.asStateFlow()

    private val _isAdmin = MutableStateFlow<Boolean>(false) // 👈 Ajout
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.usernameFlow.collect { storedUsername ->
                _username.value = storedUsername
                storedUsername?.let {
                    fetchIsExpert(it)
                    fetchIsAdmin(it) // 👈 Appel ici
                    fetchUserId(it)
                }
            }
        }

        viewModelScope.launch {
            dataStore.idFlow.collect { storedId ->
                _id.value = storedId
            }
        }

        viewModelScope.launch {
            dataStore.isExpertFlow.collect { storedExpert ->
                _isExpert.value = storedExpert
            }
        }

        viewModelScope.launch {
            dataStore.isAdminFlow.collect { storedAdmin -> // 👈 Ajout
                _isAdmin.value = storedAdmin
            }
        }
    }

    fun login(id: String, username: String) {
        _username.value = username
        _id.value = id
        viewModelScope.launch {
            dataStore.saveUsername(username)
            dataStore.saveId(id)
            fetchIsExpert(username)
            fetchIsAdmin(username) // 👈 Appel ici aussi
        }
    }

    private fun fetchIsExpert(username: String) {
        db.collection(USER_FIREBASE_COLLECTION)
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userDoc = documents.documents[0]
                    val expert = userDoc.getBoolean("expert") ?: false
                    _isExpert.value = expert
                    viewModelScope.launch {
                        dataStore.saveIsExpert(expert)
                    }
                } else {
                    _isExpert.value = false
                }
            }
            .addOnFailureListener {
                _isExpert.value = false
            }
    }

    private fun fetchIsAdmin(username: String) { // 👈 Nouvelle méthode
        db.collection(USER_FIREBASE_COLLECTION)
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userDoc = documents.documents[0]
                    val admin = userDoc.getBoolean("admin") ?: false
                    _isAdmin.value = admin
                    viewModelScope.launch {
                        dataStore.saveIsAdmin(admin)
                    }
                } else {
                    _isAdmin.value = false
                }
            }
            .addOnFailureListener {
                _isAdmin.value = false
            }
    }

    private fun fetchUserId(username: String) {
        viewModelScope.launch {
            val fetchedId = getUserIdByUsernameSuspend(db, username)
            _id.value = fetchedId
            fetchedId?.let {
                dataStore.saveId(it)
            }
        }
    }

    private suspend fun getUserIdByUsernameSuspend(
        db: FirebaseFirestore,
        username: String
    ): String? {
        return try {
            val result = db.collection(USER_FIREBASE_COLLECTION)
                .whereEqualTo("username", username)
                .get()
                .await()
            if (!result.isEmpty) result.documents[0].id else null
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStore.clearAll()
            _username.value = null
            _id.value = null
            _isExpert.value = null
            _isAdmin.value = false // 👈 Ajout
        }
    }
}
