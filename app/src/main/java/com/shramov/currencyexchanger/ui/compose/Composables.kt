package com.shramov.currencyexchanger.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.shramov.currencyexchanger.R
import com.shramov.currencyexchanger.domain.ExchangeResult
import com.shramov.currencyexchanger.ui.model.BalanceUIModel

@Composable
fun Balances(balances: State<List<BalanceUIModel>>) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = stringResource(id = R.string.my_balances),
        fontSize = 14.sp
    )
    Spacer(modifier = Modifier.height(16.dp))
    LazyRow {
        balances.value.forEach { balance ->
            item {
                Text(text = balance.balanceText)
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun ExchangeField(
    labelText: String,
    amount: State<String>,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = TextFieldValue(
            text = amount.value,
            selection = TextRange(amount.value.length)
        ),
        modifier = Modifier
            .wrapContentWidth(Alignment.End),
        onValueChange = { onValueChange(it.text) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = {
            Text(text = labelText)
        },
    )
}

@Composable
fun CurrencyPicker(
    currencies: State<List<String>>,
    selectedCurrency: String,
    onClick: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        Text(text = selectedCurrency, Modifier.clickable { isExpanded = !isExpanded })
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            Box(modifier = Modifier.size(width = 100.dp, height = 300.dp)) {
                LazyColumn {
                    items(currencies.value) {
                        DropdownMenuItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(),
                            text = { Text(it) },
                            onClick = {
                                isExpanded = false
                                onClick(it)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubmitButton(
    selectedCurrencyFrom: String,
    selectedCurrencyTo: String,
    onClick: () -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            enabled = selectedCurrencyFrom != selectedCurrencyTo
        ) {
            Text(text = "${stringResource(id = R.string.sell)} $selectedCurrencyFrom")
        }
    }
}

@Composable
fun ExchangeDialog(
    exchangeResult: State<ExchangeResult?>,
    onDismissRequest: () -> Unit
) {
    (exchangeResult.value as? ExchangeResult.Success)?.let {
        Dialog(onDismissRequest = onDismissRequest) {
            Column(
                Modifier
                    .clip(RectangleShape)
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.white))
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.currency_converted),
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
                val resultText = stringResource(
                    id = R.string.currency_convert_result,
                    it.fromAmount, it.from, it.toAmount, it.to, it.commissionFee, it.from
                )
                Text(
                    text = resultText,
                    color = colorResource(id = R.color.black)
                )
                Button(
                    onClick = onDismissRequest,
                    shape = ButtonDefaults.outlinedShape,
                    modifier = Modifier.fillMaxWidth()
                        .wrapContentWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.done))
                }
            }
        }
    }
}