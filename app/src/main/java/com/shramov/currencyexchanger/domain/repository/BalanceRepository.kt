package com.shramov.currencyexchanger.domain.repository

interface BalanceRepository {
    fun initBalance(baseCurrency: String)
    fun getBalanceForCurrency(currency: String): Double?
    fun saveBalanceForCurrency(currency: String, balance: Double)
}