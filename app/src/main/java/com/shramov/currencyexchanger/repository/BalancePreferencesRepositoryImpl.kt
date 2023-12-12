package com.shramov.currencyexchanger.repository

import android.content.SharedPreferences
import com.shramov.currencyexchanger.domain.repository.BalanceRepository
import com.shramov.currencyexchanger.utils.Const
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalancePreferencesRepositoryImpl @Inject constructor(
    private val preferences: SharedPreferences
): BalanceRepository {

    override fun initBalance(baseCurrency: String) {
        if(!preferences.getBoolean(Const.KEY_INIT, false)) {
            saveBalanceForCurrency(baseCurrency, Const.INITIAL_BALANCE)
            preferences.edit()
                .putString(Const.KEY_BASE_CURRENCY, baseCurrency)
                .putBoolean(Const.KEY_INIT, true)
                .apply()
        }
    }

    override fun saveBalanceForCurrency(currency: String, balance: Double) {
        preferences.edit()
            .putFloat(getCurrencyBalanceKey(currency), balance.toFloat())
            .apply()
    }

    override fun getBalanceForCurrency(currency: String): Double? {
        return preferences.getFloat(getCurrencyBalanceKey(currency), 0f).toDouble()
    }

    private fun getCurrencyBalanceKey(currency: String): String {
        return "${Const.KEY_BALANCE}_$currency"
    }
}