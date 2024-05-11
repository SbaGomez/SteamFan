package com.ar.sebastiangomez.steam.utils

import com.ar.sebastiangomez.steam.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class SearchHelper {
    suspend fun filterGamesBySearchTerm(gamesList: List<Game>, searchTerm: String): List<Game> {
        return withContext(Dispatchers.Default) {
            gamesList.filter { game ->
                Regex("\\b${searchTerm.lowercase(Locale.getDefault())}\\b").find(game.name.lowercase(
                    Locale.getDefault())) != null
            }
        }
    }

    fun sortFilteredGamesList(filteredGamesList: List<Game>, searchTerm: String): List<Game> {
        val exactMatch = mutableListOf<Game>()
        val partialMatchWithoutExact = mutableListOf<Game>()
        val containsSearchTerm = mutableListOf<Game>()

        searchTerm.let { term ->
            for (game in filteredGamesList) {
                val lowerCaseName = game.name.lowercase(Locale.getDefault())
                if (lowerCaseName == term.lowercase(Locale.getDefault())) {
                    exactMatch.add(game)
                } else if (lowerCaseName.contains(term.lowercase(Locale.getDefault()))) {
                    partialMatchWithoutExact.add(game)
                } else if (term.isNotEmpty() && lowerCaseName.split(" ").any { it.contains(term.lowercase(
                        Locale.getDefault())) }) {
                    containsSearchTerm.add(game)
                }
            }
        }
        return exactMatch + partialMatchWithoutExact + containsSearchTerm
    }

}