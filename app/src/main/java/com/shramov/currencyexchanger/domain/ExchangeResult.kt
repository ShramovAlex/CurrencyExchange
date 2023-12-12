package com.shramov.currencyexchanger.domain

sealed class ExchangeResult {

    data class Preview(
        val data: String
    ): ExchangeResult()

    data class Success(
        val from: String,
        val to: String,
        val fromAmount: Double,
        val toAmount: Double,
        val commissionFee: Double
    ) : ExchangeResult()

    data class Error(
        val errorMessage: String? = null
    ) : ExchangeResult()

}