package com.ar.sebastiangomez.steam.model

import androidx.room.Entity

@Entity
data class GameDetailResponse(
    val success: Boolean,
    val data: GameDetail
)
@Entity
data class GameDetail(
    val type: String,
    val name: String,
    val steam_appid: Int,
    val required_age: Int,
    val is_free: Boolean,
    val dlc: List<Int>,
    val detailed_description: String,
    val about_the_game: String,
    val short_description: String,
    val header_image: String,
    val capsule_image: String,
    val website: String?,
    val pc_requirements: Any?,
    val price_overview: PriceOverview?,
    val release_date: ReleaseDate
)
@Entity
data class ReleaseDate(
    val coming_soon: Boolean,
    val date: String,
)

@Entity
data class PriceOverview(
    val currency: String,
    val initial: Int,
    val final: Int,
    val discount_percent: Int,
    val initial_formatted: String,
    val final_formatted: String
)

@Entity
data class PcRequirements(
    val minimum: PcRequirement?,
    val recommended: PcRequirement?
)

@Entity
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