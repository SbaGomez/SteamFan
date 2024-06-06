package com.ar.sebastiangomez.steam.data.dbLocal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_detail")
data class GameDetailLocal(
    val type: String,
    val name: String?,
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
    val min_os: String,
    val min_processor: String,
    val min_memory: String,
    val min_graphics: String,
    val min_directx: String,
    val min_soundcard: String,
    val min_network: String,
    val min_storage: String,
    val req_os: String,
    val req_processor: String,
    val req_memory: String,
    val req_graphics: String,
    val req_directx: String,
    val req_soundcard: String,
    val req_network: String,
    val req_storage: String,
    //Price Overview
    val currency: String,
    val initial: Int,
    val final: Int,
    val discountPercent: Int,
    val initialFormatted: String,
    val finalFormatted: String,
    //Release Date
    val comingSoon: Boolean,
    val date: String
)