package com.shramov.currencyexchanger.domain.repository

interface CommissionRepository {
    suspend fun getFreeExchangesCount(): Int
    suspend fun deductFreeExchangesCount()
}