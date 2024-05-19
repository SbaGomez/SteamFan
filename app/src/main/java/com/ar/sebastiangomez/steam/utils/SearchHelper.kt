package com.ar.sebastiangomez.steam.utils

import com.ar.sebastiangomez.steam.model.GameInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class SearchHelper {
    suspend fun <T : GameInterface> filterGamesBySearchTerm(gamesList: List<T>, searchTerm: String): List<T> {
        return withContext(Dispatchers.Default) {
            val regex = Regex("\\b${Regex.escape(searchTerm.lowercase(Locale.getDefault()))}\\b")
            gamesList.filter { game ->
                regex.containsMatchIn(game.name.lowercase(Locale.getDefault()))
            }
        }
    }

    fun <T : GameInterface> sortFilteredGamesList(filteredGamesList: List<T>, searchTerm: String): List<T> {
        val exactMatch = mutableListOf<T>()
        val partialMatchWithoutExact = mutableListOf<T>()
        val containsSearchTerm = mutableListOf<T>()

        val lowerCaseTerm = searchTerm.lowercase(Locale.getDefault())

        for (game in filteredGamesList) {
            val lowerCaseName = game.name.lowercase(Locale.getDefault())
            when {
                lowerCaseName == lowerCaseTerm -> exactMatch.add(game)
                lowerCaseName.contains(lowerCaseTerm) -> partialMatchWithoutExact.add(game)
                lowerCaseTerm.isNotEmpty() && lowerCaseName.split(" ").any { it.contains(lowerCaseTerm) } -> containsSearchTerm.add(game)
            }
        }

        return exactMatch + partialMatchWithoutExact + containsSearchTerm
    }

}