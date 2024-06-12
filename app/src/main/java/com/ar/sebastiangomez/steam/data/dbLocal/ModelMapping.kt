package com.ar.sebastiangomez.steam.data.dbLocal

import com.ar.sebastiangomez.steam.model.GameDetail
import com.ar.sebastiangomez.steam.model.PriceOverview
import com.ar.sebastiangomez.steam.model.ReleaseDate

fun GameDetailLocal.toGameDetail() = GameDetail(
    type = type,
    name = name,
    steamAppId = id,
    requiredAge = requiredAge,
    isFree = isFree,
    dlc = arrayListOf(dlc),
    detailedDescription = "",
    aboutTheGame = aboutTheGame,
    shortDescription = shortDescription,
    headerImage = headerImage,
    capsuleImage = capsuleImage,
    website = website,
    pcRequirements = pcRequirements,
    priceOverview = PriceOverview(currency, initial, finalPrice, discountPercent, initialFormatted, finalFormatted),
    releaseDate = ReleaseDate(comingSoon, date)
)

fun GameDetail.toGameDetailLocal() = GameDetailLocal(
    type = type,
    name = name,
    id = steamAppId,
    requiredAge = requiredAge,
    isFree = isFree,
    dlc = dlc?.firstOrNull() ?: 0,
    detailedDescription = detailedDescription,
    aboutTheGame = aboutTheGame,
    shortDescription = shortDescription,
    headerImage = headerImage,
    capsuleImage = capsuleImage,
    website = website,
    pcRequirements = pcRequirements.toString(),
    currency = priceOverview?.currency ?: "",
    initial = priceOverview?.initial ?: 0,
    finalPrice = priceOverview?.final ?: 0,
    discountPercent = priceOverview?.discountPercent ?: 0,
    initialFormatted = priceOverview?.initialFormatted ?: "",
    finalFormatted = priceOverview?.finalFormatted ?: "",
    comingSoon = releaseDate.comingSoon,
    date = releaseDate.date
)