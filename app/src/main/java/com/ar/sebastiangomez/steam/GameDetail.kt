package com.ar.sebastiangomez.steam

class GameDetail {
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
        val fullgame: FullGame?,
        val header_image: String,
        val capsule_image: String,
        val capsule_imagev5: String,
        val website: String?,
        val pc_requirements: Any?, // Puedes cambiar el tipo si es un objeto con propiedades específicas
        val mac_requirements: Any?,
        val linux_requirements: Any?,
        val developers: List<String>,
        val publishers: List<String>,
        val package_groups: List<Any>, // Puedes cambiar el tipo si es un objeto con propiedades específicas
        val platforms: Platforms,
        val categories: List<Category>,
        val achievements: Achievements?,
        val release_date: ReleaseDate,
        val support_info: SupportInfo,
        val background: String,
        val background_raw: String,
        val content_descriptors: ContentDescriptors,
        val ratings: Ratings
    )

    data class FullGame(
        val appid: String,
        val name: String
    )

    data class Platforms(
        val windows: Boolean,
        val mac: Boolean,
        val linux: Boolean
    )

    data class Category(
        val id: Int,
        val description: String
    )

    data class Achievements(
        val total: Int,
        val highlighted: List<Highlighted>
    )

    data class Highlighted(
        val name: String,
        val path: String
    )

    data class ReleaseDate(
        val coming_soon: Boolean,
        val date: String
    )

    data class SupportInfo(
        val url: String,
        val email: String
    )

    data class ContentDescriptors(
        val ids: List<Any>, // Puedes cambiar el tipo si los descriptores tienen una estructura específica
        val notes: Any? // Puedes cambiar el tipo si los descriptores tienen una estructura específica
    )

    data class Ratings(
        val dejus: Dejus,
        val steam_germany: SteamGermany
    )

    data class Dejus(
        val rating_generated: String,
        val rating: String,
        val required_age: String,
        val banned: String,
        val use_age_gate: String,
        val descriptors: String
    )

    data class SteamGermany(
        val rating_generated: String,
        val rating: String,
        val required_age: String,
        val banned: String,
        val use_age_gate: String,
        val descriptors: String
    )
}