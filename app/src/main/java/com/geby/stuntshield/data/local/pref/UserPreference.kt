package com.geby.stuntshield.data.local.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }

    fun getUserId(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USER_ID] ?: ""
        }
    }

    suspend fun saveUserData(userName: String, email: String, photoUrl: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = userName
            preferences[USER_EMAIL] = email
            preferences[USER_PHOTO_URL] = photoUrl
        }
    }

    suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.remove(USER_NAME)
            preferences.remove(USER_EMAIL)
            preferences.remove(USER_PHOTO_URL)
        }
    }

    fun getUserData(): Flow<com.geby.stuntshield.data.response.UserData> {
        return dataStore.data.map { preferences ->
            com.geby.stuntshield.data.response.UserData(
                preferences[USER_NAME] ?: "",
                preferences[USER_EMAIL] ?: "",
                preferences[USER_PHOTO_URL] ?: ""
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null
        private val USER_ID = stringPreferencesKey("userId")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_PHOTO_URL = stringPreferencesKey("user_photo_url")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}