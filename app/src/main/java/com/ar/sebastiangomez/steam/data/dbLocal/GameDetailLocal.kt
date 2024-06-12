package com.ar.sebastiangomez.steam.data.dbLocal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gameDetail")
data class GameDetailLocal(
    val type: String,
    val name: String,
    @PrimaryKey val id: Int,
    val requiredAge: Int,
    val isFree: Boolean,
    val dlc: Int,
    val detailedDescription: String,
    val aboutTheGame: String,
    val shortDescription: String,
    val headerImage: String,
    val capsuleImage: String,
    val website: String?,
    //PcRequirement
    val pcRequirements: String,
    //Price Overview
    val currency: String,
    val initial: Int,
    val finalPrice: Int,
    val discountPercent: Int,
    val initialFormatted: String,
    val finalFormatted: String,
    //Release Date
    val comingSoon: Boolean,
    val date: String
)