package com.shramov.currencyexchanger.domain.useCase

import com.shramov.currencyexchanger.domain.ExchangeResult
import com.shramov.currencyexchanger.domain.repository.BalanceRepository
import com.shramov.currencyexchanger.domain.repository.CommissionRepository
import com.shramov.currencyexchanger.domain.repository.ExchangeRatesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceiveCurrencyUseCase @Inject constructor(
    exchangeRatesRepository: ExchangeRatesRepository,
    balanceRepository: BalanceRepository,
    commissionRepository: CommissionRepository
): ExchangeUseCase(exchangeRatesRepository, balanceRepository, commissionRepository) {

    override suspend fun calculateExchangeResult(
        from: String,
        to: String,
        fromRate: Double,
        toRate: Double,
        amount: Double,
        commissionPercent: Double
    ): ExchangeResult {
        return try {
            val result = (amount / toRate)
            val commission = calculateCommission(amount, commissionPercent)
            val resultWithCommission = result + commission
            ExchangeResult.Success(from, to, amount, resultWithCommission, commission)
        } catch (e: Exception) {
            ExchangeResult.Error(e.localizedMessage)
        }
    }
}