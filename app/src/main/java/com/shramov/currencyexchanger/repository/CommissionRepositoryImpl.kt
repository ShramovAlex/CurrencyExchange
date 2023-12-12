package com.shramov.currencyexchanger.repository

import android.content.SharedPreferences
import com.shramov.currencyexchanger.domain.repository.CommissionRepository
import com.shramov.currencyexchanger.utils.Const
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommissionRepositoryImpl @Inject constructor(
    private val preferences: SharedPreferences
) : CommissionRepository {

    override suspend fun getFreeExchangesCount(): Int {
        return preferences.getInt(Const.KEY_FREE_EXCHANGES, Const.DEFAULT_FREE_EXCHANGES)
    }

    override suspend fun deductFreeExchangesCount() {
        val free = getFreeExchangesCount()
        if(free > 0) {
            preferences.edit().putInt(Const.KEY_FREE_EXCHANGES, free - 1).apply()
        }
    }
}