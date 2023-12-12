package com.shramov.currencyexchanger.network

import com.shramov.currencyexchanger.network.data.CurrencyNetworkModel
import retrofit2.Response
import retrofit2.http.GET
import javax.inject.Singleton

@Singleton
interface ApiService {

    @GET("tasks/api/currency-exchange-rates")
    suspend fun getCountries(): Response<CurrencyNetworkModel>

}