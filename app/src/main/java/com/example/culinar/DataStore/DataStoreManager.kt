package com.example.culinar.DataStore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "user_prefs"
val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class DataStoreManager(private val context: Context) {

    companion object {
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val ID_KEY = stringPreferencesKey("id")
        private val IS_EXPERT_KEY = booleanPreferencesKey("is_expert")
        private val IS_ADMIN_KEY = booleanPreferencesKey("is_admin")
    }

    val usernameFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USERNAME_KEY]
    }

    val idFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[ID_KEY]
    }

    val isExpertFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_EXPERT_KEY] ?: false
    }

    val isAdminFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_ADMIN_KEY] ?: false
    }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { prefs ->
            prefs[USERNAME_KEY] = username
        }
    }

    suspend fun saveId(id: String) {
        context.dataStore.edit { prefs ->
            prefs[ID_KEY] = id
        }
    }

    suspend fun saveIsExpert(isExpert: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_EXPERT_KEY] = isExpert
        }
    }

    suspend fun saveIsAdmin(isAdmin: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_ADMIN_KEY] = isAdmin
        }
    }

    suspend fun clearUsername() {
        context.dataStore.edit { prefs ->
            prefs.remove(USERNAME_KEY)
        }
    }

    suspend fun clearId() {
        context.dataStore.edit { prefs ->
            prefs.remove(ID_KEY)
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
