package com.example.culinar.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "user_prefs"
val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class DataStoreManager(private val context: Context) {

    companion object {
        val USERNAME_KEY = stringPreferencesKey("username")
    }

    val usernameFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USERNAME_KEY]
    }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { prefs ->
            prefs[USERNAME_KEY] = username
        }
    }

    suspend fun clearUsername() {
        context.dataStore.edit { prefs ->
            prefs.remove(USERNAME_KEY)
        }
    }
}
