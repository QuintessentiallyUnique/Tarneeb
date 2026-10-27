package com.qusai.tarneebscoretracker.data

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.qusai.tarneebscoretracker.model.GameState
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.gameDataStore by preferencesDataStore(name = "tarneeb_game")

class LocalGameStorage(private val context: Context) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    val savedGame: Flow<GameState?> = context.gameDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[GAME_STATE_KEY]?.let { encoded ->
                runCatching { json.decodeFromString(GameState.serializer(), encoded) }.getOrNull()
            }
        }

    suspend fun save(gameState: GameState) {
        val encoded = json.encodeToString(GameState.serializer(), gameState)
        context.gameDataStore.edit { preferences: MutablePreferences ->
            preferences[GAME_STATE_KEY] = encoded
        }
    }

    private companion object {
        val GAME_STATE_KEY: Preferences.Key<String> = stringPreferencesKey("game_state_json")
    }
}
