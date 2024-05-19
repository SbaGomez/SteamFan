package com.ar.sebastiangomez.steam.model

import com.google.gson.annotations.SerializedName

interface GameInterface {
    val name: String
}

data class Game(val id: String, override val name: String) : GameInterface {
    override fun toString(): String {
        return "Game(id=$id, name='$name')"
    }
}

data class GameCached(val id: String, override val name: String, var image: String) : GameInterface {
    override fun toString(): String {
        return "GameCached(id=$id, name='$name', image='$image')"
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