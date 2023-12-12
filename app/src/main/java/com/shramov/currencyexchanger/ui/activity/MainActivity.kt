package com.shramov.currencyexchanger.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shramov.currencyexchanger.R
import com.shramov.currencyexchanger.ui.compose.Balances
import com.shramov.currencyexchanger.ui.compose.CurrencyPicker
import com.shramov.currencyexchanger.ui.compose.ExchangeDialog
import com.shramov.currencyexchanger.ui.compose.ExchangeField
import com.shramov.currencyexchanger.ui.compose.SubmitButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainScreen() }
    }

    @Preview
    @Composable
    fun MainScreen(viewModel: MainViewModel = viewModel()) {
        val currencies = viewModel.currenciesUI.observeAsState(listOf())
        val receivePreviewAmount = viewModel.receiveResultPreview.observeAsState("")
        val sellAmount = viewModel.sellResultPreview.observeAsState("")
        var selectedCurrencyFrom by remember { mutableStateOf("EUR") }
        var selectedCurrencyTo by remember { mutableStateOf("USD") }
        val exchangeResult = viewModel.showExchangeDialog.observeAsState()
        ExchangeDialog(
            exchangeResult = exchangeResult
        ) {
            viewModel.closeExchangeDialog()
        }
        Row {
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Balances(viewModel.balancesUI.observeAsState(emptyList()))
                Text(text = stringResource(id = R.string.currency_exchange))
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    ExchangeField(
                        stringResource(id = R.string.sell),
                        amount = sellAmount,
                        onValueChange = {
                            viewModel.sellPreview(selectedCurrencyFrom, selectedCurrencyTo, it)
                        }
                    )
                    CurrencyPicker(
                        currencies = currencies, selectedCurrency = selectedCurrencyFrom) {
                        selectedCurrencyFrom = it
                        viewModel.sellPreview(selectedCurrencyFrom, selectedCurrencyTo, sellAmount.value)
                    }
                }
                Row {
                    ExchangeField(
                        stringResource(id = R.string.receive),
                        amount = receivePreviewAmount,
                        onValueChange = {
                            viewModel.buyPreview(selectedCurrencyFrom, selectedCurrencyTo, it)
                        }
                    )
                    CurrencyPicker(
                        currencies = currencies, selectedCurrency = selectedCurrencyTo) {
                        selectedCurrencyTo = it
                        viewModel.buyPreview(selectedCurrencyFrom, selectedCurrencyTo, receivePreviewAmount.value)
                    }
                }
                SubmitButton(
                    selectedCurrencyFrom,
                    selectedCurrencyTo
                ) {
                    viewModel.submitExchange(selectedCurrencyFrom, selectedCurrencyTo)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    }

}