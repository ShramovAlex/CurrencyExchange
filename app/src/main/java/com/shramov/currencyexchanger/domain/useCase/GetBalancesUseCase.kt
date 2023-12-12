package com.shramov.currencyexchanger.domain.useCase

import com.shramov.currencyexchanger.domain.model.Balance
import com.shramov.currencyexchanger.domain.repository.BalanceRepository
import com.shramov.currencyexchanger.domain.repository.ExchangeRatesRepository
import javax.inject.Inject

class GetBalancesUseCase @Inject constructor(
    private val exchangeRatesRepository: ExchangeRatesRepository,
    private val balanceRepository: BalanceRepository
) {

    suspend fun execute(): List<Balance> {
        return exchangeRatesRepository.getExchangeRates(forceUpdate = false)?.let { exchangeRate ->
            val balances = mutableListOf<Balance>()
            exchangeRate.rates.keys.forEach {
                balanceRepository.getBalanceForCurrency(it)?.let { balance ->
                    if(balance > 0) {
                        balances.add(Balance(it, balance))
                    }
                }
            }
            return balances
        } ?: emptyList()
    }

}