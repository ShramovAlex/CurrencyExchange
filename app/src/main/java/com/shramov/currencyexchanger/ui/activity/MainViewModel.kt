package com.shramov.currencyexchanger.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shramov.currencyexchanger.domain.ExchangeResult
import com.shramov.currencyexchanger.domain.useCase.ReceiveCurrencyUseCase
import com.shramov.currencyexchanger.domain.useCase.GetBalancesUseCase
import com.shramov.currencyexchanger.domain.useCase.SellCurrencyUseCase
import com.shramov.currencyexchanger.domain.useCase.SynchronizeUseCase
import com.shramov.currencyexchanger.ui.model.BalanceUIModel
import com.shramov.currencyexchanger.ui.model.fromDomain
import com.shramov.currencyexchanger.utils.Const
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sellCurrencyUseCase: SellCurrencyUseCase,
    private val receiveCurrencyUseCase: ReceiveCurrencyUseCase,
    private val synchronizeUseCase: SynchronizeUseCase,
    private val getBalancesUseCase: GetBalancesUseCase,
    private val bgDispatcher: CoroutineDispatcher
): ViewModel() {

    val currenciesUI = MutableLiveData<List<String>>()
    val balancesUI = MutableLiveData<List<BalanceUIModel>>()
    val receiveResultPreview = MutableLiveData<String>()
    val sellResultPreview = MutableLiveData<String>()
    val showExchangeDialog = MutableLiveData<ExchangeResult>()
    private var sellAmount = 0.0

    private var currencySynchronizationJob: Job? = null

    init {
        startCurrencySynchronization()
    }

    fun sellPreview(from: String, to: String, amountStr: String) = viewModelScope.launch(bgDispatcher) {
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        val limitedAmount = sellCurrencyUseCase.limitAmount(from, amount)
        if(amount != limitedAmount) {
            sellResultPreview.postValue(limitedAmount.toString())
        } else {
            sellResultPreview.postValue(amountStr)
        }
        sellAmount = limitedAmount
        val result = sellCurrencyUseCase.calculateExchangeCurrencyResultFormatted(from, to, limitedAmount)
        if(result is ExchangeResult.Preview) {
            receiveResultPreview.postValue(result.data)
        }
    }

    fun buyPreview(from: String, to: String, amountStr: String) = viewModelScope.launch(bgDispatcher) {
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        val result = receiveCurrencyUseCase.calculateExchangeCurrencyResultFormatted(from, to, amount)
        if(result is ExchangeResult.Preview) {
            sellResultPreview.postValue(result.data)
            sellAmount = result.data.toDoubleOrNull() ?: 0.0
        }
        receiveResultPreview.postValue(amountStr)
    }

    fun submitExchange(from: String, to: String) = viewModelScope.launch(bgDispatcher) {
        val result = sellCurrencyUseCase.submitExchange(from, to, sellAmount)
        sellResultPreview.postValue("")
        receiveResultPreview.postValue("")
        sellAmount = 0.0
        updateBalance()
        showExchangeDialog.postValue(result)
    }

    fun closeExchangeDialog() {
        showExchangeDialog.value = null
    }

    private fun startCurrencySynchronization() {
        stopCurrencySynchronization()
        currencySynchronizationJob = viewModelScope.launch(bgDispatcher) {
            while(isActive) {
                val currencies = synchronizeUseCase.getSynchronizationJob()
                updateBalance()
                currenciesUI.postValue(currencies)
                delay(Const.SYNCHRONIZATION_DELAY_MS)
            }
        }
    }

    private suspend fun updateBalance() {
        balancesUI.postValue(getBalancesUseCase.execute().fromDomain())
    }

    private fun stopCurrencySynchronization() {
        currencySynchronizationJob?.cancel()
        currencySynchronizationJob = null
    }

    override fun onCleared() {
        stopCurrencySynchronization()
        super.onCleared()
    }
}