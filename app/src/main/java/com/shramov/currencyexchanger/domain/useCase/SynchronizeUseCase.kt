package com.shramov.currencyexchanger.domain.useCase

import com.shramov.currencyexchanger.domain.repository.BalanceRepository
import com.shramov.currencyexchanger.domain.repository.ExchangeRatesRepository
import com.shramov.currencyexchanger.utils.Const
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SynchronizeUseCase @Inject constructor(
    private val exchangeRatesRepository: ExchangeRatesRepository,
    private val balanceRepository: BalanceRepository
) {

    suspend fun getSynchronizationJob(): List<String> {
        exchangeRatesRepository.getExchangeRates(forceUpdate = true)?.let { currencyNetworkModel ->
            currencyNetworkModel.base?.let { balanceRepository.initBalance(it) }
            exchangeRatesRepository.saveExchangeRates(currencyNetworkModel)
            return currencyNetworkModel.rates.keys.toList()
        }
        return emptyList()
    }

}