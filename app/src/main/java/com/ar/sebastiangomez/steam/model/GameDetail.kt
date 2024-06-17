package com.ar.sebastiangomez.steam.model

import com.google.gson.annotations.SerializedName
data class GameDetailResponse(
    val success: Boolean,
    val data: GameDetail
)

data class GameDetail(
    val type: String,
    val name: String,
    @SerializedName("steam_appid") val steamAppId: Int,
    @SerializedName("required_age") val requiredAge: Int,
    @SerializedName("is_free") val isFree: Boolean,
    val dlc: List<Int>?,
    @SerializedName("detailed_description") val detailedDescription: String,
    @SerializedName("about_the_game") val aboutTheGame: String,
    @SerializedName("short_description") val shortDescription: String,
    @SerializedName("header_image") val headerImage: String,
    @SerializedName("capsule_image") val capsuleImage: String,
    val website: String?,
    @SerializedName("pc_requirements") val pcRequirements: Any?,
    @SerializedName("price_overview") val priceOverview: PriceOverview?,
    @SerializedName("release_date") val releaseDate: ReleaseDate
)

data class ReleaseDate(
    @SerializedName("coming_soon") val comingSoon: Boolean,
    val date: String,
)

data class PriceOverview(
    val currency: String,
    val initial: Int,
    val final: Int,
    @SerializedName("discount_percent") val discountPercent: Int,
    @SerializedName("initial_formatted") val initialFormatted: String,
    @SerializedName("final_formatted") val finalFormatted: String
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