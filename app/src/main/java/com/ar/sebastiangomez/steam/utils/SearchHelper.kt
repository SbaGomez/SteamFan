package com.ar.sebastiangomez.steam.utils

import com.ar.sebastiangomez.steam.model.GameInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class SearchHelper {

    suspend fun <T : GameInterface> filterGamesBySearchTerm(gamesList: List<T>, searchTerm: String): List<T> {
        return withContext(Dispatchers.Default) {
            val lowerCaseTerm = searchTerm.lowercase(Locale.getDefault())
            val exactMatch = mutableListOf<T>()
            val partialMatchWithoutExact = mutableListOf<T>()
            val containsSearchTerm = mutableListOf<T>()

            for (game in gamesList) {
                val lowerCaseName = game.name.lowercase(Locale.getDefault())
                when {
                    lowerCaseName == lowerCaseTerm -> exactMatch.add(game)
                    lowerCaseName.contains(lowerCaseTerm) -> partialMatchWithoutExact.add(game)
                    lowerCaseTerm.isNotEmpty() && lowerCaseName.split(" ").any { it.contains(lowerCaseTerm) } -> containsSearchTerm.add(game)
                }
            }

            exactMatch + partialMatchWithoutExact + containsSearchTerm
        }
    }

}