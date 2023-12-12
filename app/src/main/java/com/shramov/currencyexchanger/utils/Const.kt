package com.shramov.currencyexchanger.utils

object Const {
    const val INITIAL_BALANCE = 1000.0
    const val SHARED_PREFERENCES_NAME = "CurrencyExchange"
    const val SYNCHRONIZATION_DELAY_MS = 5000L
    const val EXCHANGE_FORMAT = "%.2f"
    const val DEFAULT_COMMISSION = 0.007
    const val DEFAULT_FREE_EXCHANGES = 5

    const val KEY_INIT = "KEY_INIT"
    const val KEY_BASE_CURRENCY = "KEY_BASE_CURRENCY"
    const val KEY_BALANCE = "KEY_BALANCE_"
    const val KEY_FREE_EXCHANGES = "KEY_FREE_EXCHANGES"
}