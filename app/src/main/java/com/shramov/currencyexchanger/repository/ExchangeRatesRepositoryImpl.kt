package com.shramov.currencyexchanger.repository

import com.shramov.currencyexchanger.domain.model.Currency
import com.shramov.currencyexchanger.domain.repository.ExchangeRatesRepository
import com.shramov.currencyexchanger.network.ApiService
import com.shramov.currencyexchanger.network.data.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRatesRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): ExchangeRatesRepository {

    @Volatile
    private var exchangeRates: Currency? = null

    override suspend fun getExchangeRates(forceUpdate: Boolean): Currency? {
        return if(exchangeRates != null && !forceUpdate) {
            exchangeRates
        } else {
            apiService.getCountries().body()?.toDomain()
        }
    }

    override suspend fun saveExchangeRates(currency: Currency) {
        exchangeRates = currency
    }
}