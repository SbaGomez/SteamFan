package com.ar.sebastiangomez.steam.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.SQLException
import android.util.Log
import android.widget.Toast
import com.ar.sebastiangomez.steam.data.dbLocal.AppDataBase
import com.ar.sebastiangomez.steam.data.dbLocal.toGameDetail
import com.ar.sebastiangomez.steam.data.dbLocal.toGameDetailLocal
import com.ar.sebastiangomez.steam.model.Game
import com.ar.sebastiangomez.steam.model.GameCached
import com.ar.sebastiangomez.steam.model.GameDetail
import com.ar.sebastiangomez.steam.ui.BookmarkActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val tag = "LOG-API"
class GamesDataSource {
    companion object{
        private const val API_BASE_URL = "https://api.steampowered.com/"
        private const val API_BASE_DETAIL_URL = "https://store.steampowered.com/"

        @SuppressLint("StaticFieldLeak")
        private val firestore = FirebaseFirestore.getInstance()
        private var firebaseAuth = FirebaseAuth.getInstance()

        private val apiGames : SteamApiService = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(SteamApiService::class.java)

        private val apiDetails: SteamApiService = Retrofit.Builder()
            .baseUrl(API_BASE_DETAIL_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SteamApiService::class.java)

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

        suspend fun deleteRoom(gameId: String, context: Context) {
            val db = AppDataBase.getInstance(context)
            val gameDetailLocal = db.gameDetailsDao().getByPK(gameId)
            if (gameDetailLocal != null) {
                db.gameDetailsDao().delete(gameDetailLocal)
            }
        }

        suspend fun getDetails(gameId: String, context: Context): GameDetail? {

            // Recupero la informacion localmente (si existe)
            val db = AppDataBase.getInstance(context)
            val gameDetailLocal = db.gameDetailsDao().getByPK(gameId)

            if (gameDetailLocal != null) {
                Log.d(tag, "Game details local: ${gameDetailLocal.toGameDetail()}")
                Log.d(tag, "Game details found in local database for ID: $gameId")
                return gameDetailLocal.toGameDetail()
            }

            Log.d(tag, "Fetching details for game ID: $gameId")

            return try {
                val response = apiDetails.getGameDetails(gameId)
                val gameDetailResponse = response[gameId]
                if (gameDetailResponse?.success == true) {
                    val gameDetail = gameDetailResponse.data
                    Log.d(tag, "Game details: $gameDetail")

                    try {
                        db.gameDetailsDao().insert(gameDetail.toGameDetailLocal())
                        // Si no se lanzó una excepción, la inserción fue exitosa
                        Log.d(tag, "Game details inserted successfully")
                    } catch (e: SQLException) {
                        // Manejo de excepciones de SQL
                        Log.e(tag, "ERROR: SQL Exception occurred: ${e.message}")
                    } catch (e: Exception) {
                        // Otro tipo de excepción
                        Log.e(tag, "ERROR: Failed to insert game details into the local database: ${e.message}")
                    }
                    return gameDetail
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

        suspend fun saveGameCached(context: Context,gameCached: GameCached): Boolean {
            return try {
                val userId = firebaseAuth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
                val gameExists = exists(gameCached.id)

                if (gameExists) {
                    Log.d(tag, "Game already exists: $gameCached")
                    false
                } else {
                    firestore.collection("users")
                        .document(userId)
                        .collection("games")
                        .document(gameCached.id)
                        .set(gameCached)
                        .await()
                    Toast.makeText(context, "Agregaste - ${gameCached.name} - de favoritos.", Toast.LENGTH_LONG).show()
                    Log.d(tag, "Game cached saved successfully: $gameCached")
                    // Redirigir a HomeActivity
                    val intent = Intent(context, BookmarkActivity::class.java)
                    context.startActivity(intent)
                    true
                }
            } catch (e: Exception) {
                Log.e(tag, "ERROR: Failed to save game cached: ${e.message}")
                false
            }
        }

        suspend fun exists(gameId: String): Boolean {
            return try {
                val userId = firebaseAuth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
                val document = firestore.collection("users")
                    .document(userId)
                    .collection("games")
                    .document(gameId)
                    .get()
                    .await()

                document.exists()
            } catch (e: Exception) {
                Log.e(tag, "ERROR: Failed to check document existence: ${e.message}")
                false
            }
        }

        suspend fun getUserGameCached(): List<GameCached> {
            val userId = firebaseAuth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
            return try {
                val snapshot = firestore.collection("users")
                    .document(userId)
                    .collection("games")
                    .get()
                    .await()

                val games = snapshot.documents.mapNotNull { it.toObject(GameCached::class.java) }
                Log.d(tag, "Fetched ${games.size} cached games for user $userId")
                games
            } catch (e: Exception) {
                Log.e(tag, "ERROR: Failed to fetch cached games: ${e.message}")
                emptyList()
            }
        }

        fun removeGameCached(context: Context, gameId: String, gameName: String, activity: String? = null): Boolean {
            val userId = firebaseAuth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
            return try {
                firestore.collection("users")
                    .document(userId)
                    .collection("games")
                    .document(gameId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Eliminaste - $gameName - de favoritos.", Toast.LENGTH_LONG).show()
                        Log.d(tag, "Game cached removed successfully for ID: $gameId")
                        if (activity == "BookmarkActivity") {
                            (context as BookmarkActivity).recreate()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(tag, "ERROR: Failed to remove game cached: ${e.message}")
                    }
                true
            } catch (e: Exception) {
                Log.e(tag, "ERROR: Failed to remove game cached: ${e.message}")
                false
            }
        }

        suspend fun countAllGames(): Int {
            val userId = firebaseAuth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
            return try {
                val snapshot = firestore.collection("users")
                    .document(userId)
                    .collection("games")
                    .get()
                    .await()

                val gameCount = snapshot.size()
                Log.d(tag, "Total games count for user $userId: $gameCount")
                gameCount
            } catch (e: Exception) {
                Log.e(tag, "ERROR: Failed to fetch game count: ${e.message}")
                0
            }
        }

    }
}