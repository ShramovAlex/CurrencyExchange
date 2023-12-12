package com.shramov.currencyexchanger.ui.model

import com.shramov.currencyexchanger.domain.model.Balance
import com.shramov.currencyexchanger.utils.Const

data class BalanceUIModel(
    val currency: String,
    val balance: Double,
    val balanceText: String
)
fun List<Balance>.fromDomain(): List<BalanceUIModel> {
    return this.map {
        BalanceUIModel(it.currencyName, it.balance, "${Const.EXCHANGE_FORMAT.format(it.balance)} ${it.currencyName}")
    }
}