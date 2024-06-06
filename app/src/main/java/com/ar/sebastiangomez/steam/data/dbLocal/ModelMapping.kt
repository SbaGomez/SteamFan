package com.ar.sebastiangomez.steam.data.dbLocal

import com.ar.sebastiangomez.steam.model.GameDetail
import com.ar.sebastiangomez.steam.model.PcRequirement
import com.ar.sebastiangomez.steam.model.PcRequirements
import com.ar.sebastiangomez.steam.model.PriceOverview
import com.ar.sebastiangomez.steam.model.ReleaseDate

fun GameDetailLocal.toGameDetail() = GameDetail(
    type = type,
    name = name ?: "",
    steamAppId = id,
    requiredAge = requiredAge,
    isFree = isFree,
    dlc = arrayListOf(dlc),
    detailedDescription = detailedDescription,
    aboutTheGame = aboutTheGame,
    shortDescription = shortDescription,
    headerImage = headerImage,
    capsuleImage = capsuleImage,
    website = website,
    pcRequirements = PcRequirements(
        PcRequirement(min_os, min_processor, min_memory, min_graphics, min_directx, min_soundcard, min_network, min_storage),
        PcRequirement(req_os, req_processor, req_memory, req_graphics, req_directx, req_soundcard, req_network, req_storage)
    ),
    priceOverview = PriceOverview(currency, initial, final, discountPercent, initialFormatted, finalFormatted),
    releaseDate = ReleaseDate(comingSoon, date)
)
fun GameDetail.toGameDetailLocal() = GameDetailLocal(
    type = type,
    name = name,
    id = steamAppId,
    requiredAge = requiredAge,
    isFree = isFree,
    dlc = dlc.firstOrNull() ?: 0,
    detailedDescription = detailedDescription,
    aboutTheGame = aboutTheGame,
    shortDescription = shortDescription,
    headerImage = headerImage,
    capsuleImage = capsuleImage,
    website = website,
    min_os = (pcRequirements as? PcRequirements)?.minimum?.os ?: "",
    min_processor = (pcRequirements as? PcRequirements)?.minimum?.processor ?: "",
    min_memory = (pcRequirements as? PcRequirements)?.minimum?.memory ?: "",
    min_graphics = (pcRequirements as? PcRequirements)?.minimum?.graphics ?: "",
    min_directx = (pcRequirements as? PcRequirements)?.minimum?.directx ?: "",
    min_soundcard = (pcRequirements as? PcRequirements)?.minimum?.soundcard ?: "",
    min_network = (pcRequirements as? PcRequirements)?.minimum?.network ?: "",
    min_storage = (pcRequirements as? PcRequirements)?.minimum?.storage ?: "",
    req_os = (pcRequirements as? PcRequirements)?.recommended?.os ?: "",
    req_processor = (pcRequirements as? PcRequirements)?.recommended?.processor ?: "",
    req_memory = (pcRequirements as? PcRequirements)?.recommended?.memory ?: "",
    req_graphics = (pcRequirements as? PcRequirements)?.recommended?.graphics ?: "",
    req_directx = (pcRequirements as? PcRequirements)?.recommended?.directx ?: "",
    req_soundcard = (pcRequirements as? PcRequirements)?.recommended?.soundcard ?: "",
    req_network = (pcRequirements as? PcRequirements)?.recommended?.network ?: "",
    req_storage = (pcRequirements as? PcRequirements)?.recommended?.storage ?: "",
    currency = priceOverview?.currency ?: "",
    initial = priceOverview?.initial ?: 0,
    final = priceOverview?.final ?: 0,
    discountPercent = priceOverview?.discountPercent ?: 0,
    initialFormatted = priceOverview?.initialFormatted ?: "",
    finalFormatted = priceOverview?.finalFormatted ?: "",
    comingSoon = releaseDate.comingSoon,
    date = releaseDate.date
)