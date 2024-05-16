package com.ar.sebastiangomez.steam.model

data class GameDetailResponse(
    val success: Boolean,
    val data: GameDetail
)

data class GameDetail(
    val type: String,
    val name: String,
    val steam_appid: Int,
    val required_age: Int,
    val is_free: Boolean,
    val detailed_description: String,
    val about_the_game: String,
    val short_description: String,
    val header_image: String,
    val capsule_image: String,
    val website: String?,
    val pc_requirements: Any?,
)

data class PcRequirements(
    val minimum: PcRequirement?,
    val recommended: PcRequirement?
)

data class PcRequirement(
    val os: String,
    val processor: String,
    val memory: String,
    val graphics: String,
    val directx: String,
    val soundcard: String,
    val network: String,
    val storage: String
)