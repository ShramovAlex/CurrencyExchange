package com.shramov.currencyexchanger.domain.useCase

import com.shramov.currencyexchanger.domain.ExchangeResult
import com.shramov.currencyexchanger.domain.repository.BalanceRepository
import com.shramov.currencyexchanger.domain.repository.CommissionRepository
import com.shramov.currencyexchanger.domain.repository.ExchangeRatesRepository
import com.shramov.currencyexchanger.utils.Const

abstract class ExchangeUseCase(
    private val exchangeRatesRepository: ExchangeRatesRepository,
    private val balanceRepository: BalanceRepository,
    private val commissionRepository: CommissionRepository
) {
    private var baseCurrency: String? = null
    private var currencyRatesToBase: Map<String, Double> = emptyMap()

    suspend fun submitExchange(
        from: String,
        to: String,
        amount: Double?
    ): ExchangeResult {
        if(amount == null || amount == 0.0) return ExchangeResult.Error()

        val exchangeResult = calculateExchangeCurrencyResult(from, to, amount)
        if(exchangeResult is ExchangeResult.Success) {
            val currentFromBalance = balanceRepository.getBalanceForCurrency(from) ?: 0.0
            val currentToBalance = balanceRepository.getBalanceForCurrency(to) ?: 0.0
            val newFromBalance = currentFromBalance - amount
            val newToBalance = currentToBalance + exchangeResult.toAmount
            balanceRepository.saveBalanceForCurrency(from, newFromBalance)
            balanceRepository.saveBalanceForCurrency(to, newToBalance)
            commissionRepository.deductFreeExchangesCount()
        }
        return exchangeResult
    }

    suspend fun calculateExchangeCurrencyResultFormatted(
        from: String,
        to: String,
        amount: Double?
    ): ExchangeResult {
        val exchangeResult = calculateExchangeCurrencyResult(from, to, amount)
        return if(exchangeResult is ExchangeResult.Success) {
            ExchangeResult.Preview(Const.EXCHANGE_FORMAT.format(exchangeResult.toAmount))
        } else {
            exchangeResult
        }
    }

    fun limitAmount(currency: String, amount: Double): Double {
        val balance = balanceRepository.getBalanceForCurrency(currency) ?: 0.0
        if(amount > balance) {
            return balance
        }
        return amount
    }

    private suspend fun calculateExchangeCurrencyResult(
        from: String,
        to: String,
        amount: Double?
    ): ExchangeResult {
        if(amount == null || amount == 0.0) return ExchangeResult.Error()
        val exchangeAmount = limitAmount(from, amount)
        val exchangeRates = exchangeRatesRepository.getExchangeRates(forceUpdate = false)
        baseCurrency = exchangeRates?.base
        currencyRatesToBase = exchangeRates?.rates ?: emptyMap()
        val commissionFeePercent = exchangeRates?.commissionFee ?: Const.DEFAULT_COMMISSION
        val fromRate = currencyRatesToBase[from] ?: return ExchangeResult.Error()
        val toRate = currencyRatesToBase[to] ?: return ExchangeResult.Error()
        val normalizedAmount = convertToBaseRate(exchangeAmount)
        return calculateExchangeResult(
            from,
            to,
            fromRate,
            toRate,
            normalizedAmount,
            commissionFeePercent
        )
    }

    protected abstract suspend fun calculateExchangeResult(
        from: String,
        to: String,
        fromRate: Double,
        toRate: Double,
        amount: Double,
        commissionPercent: Double
    ): ExchangeResult

    protected suspend fun calculateCommission(value: Double, commission: Double): Double {
        return if(commissionRepository.getFreeExchangesCount() > 0) {
            0.0
        } else (value * commission)
    }

    private fun convertToBaseRate(amount: Double): Double {
        val baseRate = currencyRatesToBase[baseCurrency] ?: return 0.0
        return amount * baseRate
    }
}