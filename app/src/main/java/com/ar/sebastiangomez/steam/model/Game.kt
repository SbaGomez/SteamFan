package com.ar.sebastiangomez.steam.model

import com.google.gson.annotations.SerializedName

data class Game(val id: String, val name: String) {
    override fun toString(): String {
        return "Game(id=$id, name='$name')"
    }
}

data class SteamAppListResponse(
    @SerializedName("applist") val appList: AppList
)

data class AppList(
    @SerializedName("apps") val apps: List<SteamApp>
)

data class SteamApp(
    @SerializedName("appid") val id: String,
    @SerializedName("name") val name: String
)