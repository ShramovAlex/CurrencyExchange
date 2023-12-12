package com.shramov.currencyexchanger.network.data

import com.shramov.currencyexchanger.domain.model.Currency
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CurrencyNetworkModel(
    @Json(name="base") var base  : String? = null,
    @Json(name="date") var date  : String? = null,
    @Json(name="rates") var rates : Map<String, Double>? = null
)

fun CurrencyNetworkModel.toDomain(): Currency {
    return Currency(
        this.base ?: "",
        this.date ?: "",
        this.rates ?: emptyMap()
    )
}