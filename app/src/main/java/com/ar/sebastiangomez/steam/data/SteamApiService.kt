package com.ar.sebastiangomez.steam.data

import com.ar.sebastiangomez.steam.model.GameDetailResponse
import com.ar.sebastiangomez.steam.model.SteamAppListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SteamApiService {
    @GET("ISteamApps/GetAppList/v2/")
    suspend fun getAppList(): SteamAppListResponse

    @GET("api/appdetails/")
    suspend fun getGameDetails(@Query("appids") gameId: String): Map<String, GameDetailResponse>

}