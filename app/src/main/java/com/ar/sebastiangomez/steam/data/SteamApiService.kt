package com.ar.sebastiangomez.steam.data

import com.ar.sebastiangomez.steam.model.SteamAppListResponse
import retrofit2.http.GET

interface SteamApiService {
    @GET("ISteamApps/GetAppList/v2/")
    suspend fun getAppList(): SteamAppListResponse
}