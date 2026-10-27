package com.qusai.tarneebscoretracker.data

import com.qusai.tarneebscoretracker.model.GameState
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    val gameState: Flow<GameState?>
    suspend fun saveGame(gameState: GameState)
}

class DefaultGameRepository(
    private val storage: LocalGameStorage,
) : GameRepository {
    override val gameState: Flow<GameState?> = storage.savedGame

    override suspend fun saveGame(gameState: GameState) = storage.save(gameState)
}
