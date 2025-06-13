package com.example.culinar.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinar.datastore.DataStoreManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SessionViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = DataStoreManager(application)

    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.usernameFlow.collect {
                _username.value = it
            }
        }
    }

    fun login(user: String) {
        viewModelScope.launch {
            dataStore.saveUsername(user)
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStore.clearUsername()
        }
    }
}
