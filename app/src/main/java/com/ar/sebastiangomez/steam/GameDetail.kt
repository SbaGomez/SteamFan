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
        val background: String,
        val background_raw: String,
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
}