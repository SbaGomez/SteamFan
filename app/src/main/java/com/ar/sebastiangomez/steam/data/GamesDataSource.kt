package com.ar.sebastiangomez.steam.data

import android.util.Log
import com.ar.sebastiangomez.steam.model.Game
import com.ar.sebastiangomez.steam.model.GameDetail
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val tag = "LOG-API"
class GamesDataSource {
    companion object{
        private const val API_BASE_URL = "https://api.steampowered.com/"
        private const val API_BASE_DETAIL_URL = "https://store.steampowered.com/"

        private val apiGames : SteamApiService = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(SteamApiService::class.java)

        private val apiDetails: SteamApiService = Retrofit.Builder()
            .baseUrl(API_BASE_DETAIL_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SteamApiService::class.java)

        private val gameDetailsCache = mutableMapOf<String, GameDetail?>()

        suspend fun getGames(): List<Game> {
            Log.d(tag, "Games DataSource Get")

            return try {
                val response = apiGames.getAppList()
                Log.d(tag, "Juegos: ${response.appList.apps}")

                response.appList.apps
                    .map { steamApp -> Game(steamApp.id, steamApp.name) }
                    .filter { game -> game.name.isNotEmpty() }
            } catch (e: Exception) {
                Log.e(tag, "ERROR: Failed to fetch game list: ${e.message}")
                emptyList()
            }
        }

        suspend fun getDetails(gameId: String): GameDetail? {
            // Check cache first
            gameDetailsCache[gameId]?.let {
                Log.d(tag, "Fetching Game details found in cache for ID: $gameId")
                return it
            }

            Log.d(tag, "Fetching details for game ID: $gameId")

            return try {
                val response = apiDetails.getGameDetails(gameId)
                val gameDetailResponse = response[gameId]
                if (gameDetailResponse?.success == true) {
                    val gameDetail = gameDetailResponse.data
                    Log.d(tag, "Game details: $gameDetail")
                    // Store in cache
                    gameDetailsCache[gameId] = gameDetail
                    gameDetail
                } else {
                    Log.e(tag, "ERROR: Unsuccessful response or success flag is false")
                    null
                }
            } catch (e: Exception) {
                Log.e(tag, "ERROR: Failed to fetch game details: ${e.message}")
                null
            }
        }

        suspend fun isSuccess(gameId: String): Boolean? {
            Log.d(tag, "Fetching isSuccess for game ID: $gameId")

            return try {
                val response = apiDetails.getGameDetails(gameId)
                val gameDetailResponse = response[gameId]
                val isSuccess = gameDetailResponse?.success
                Log.d(tag, "Game isSuccess: $isSuccess")
                isSuccess
            } catch (e: Exception) {
                Log.e(tag, "ERROR: Failed to fetch game details: ${e.message}")
                null
            }
        }

        suspend fun getImage(gameId: String): String? {
            Log.d(tag, "Fetching image for game ID: $gameId")

            return try {
                val response = apiDetails.getGameDetails(gameId)
                val gameDetailResponse = response[gameId]
                if (gameDetailResponse?.success == true) {
                    val headerImage = gameDetailResponse.data.headerImage
                    Log.d(tag, "Header image fetched successfully for game ID: $gameId")
                    headerImage // Return the header image URL
                } else {
                    Log.e(tag, "ERROR: Unsuccessful response or success flag is false")
                    null
                }
            } catch (e: Exception) {
                Log.e(tag, "ERROR: Failed to fetch game image: ${e.message}")
                null
            }
        }

        suspend fun countGames(): Int {
            Log.d(tag, "Games DataSource Get Count")

            return try {
                val response = apiGames.getAppList()
                Log.d(tag, "Total de juegos: ${response.appList.apps.size}")
                response.appList.apps.size
            } catch (e: Exception) {
                Log.e(tag, "ERROR: Failed to fetch game count: ${e.message}")
                0
            }
        }
    }
}