package com.shramov.currencyexchanger.domain.repository

import com.shramov.currencyexchanger.domain.model.Currency

interface ExchangeRatesRepository {
    suspend fun getExchangeRates(forceUpdate: Boolean): Currency?
    suspend fun saveExchangeRates(currency: Currency)
}