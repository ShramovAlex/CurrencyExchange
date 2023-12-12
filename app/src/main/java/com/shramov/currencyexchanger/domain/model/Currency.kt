package com.shramov.currencyexchanger.domain.model

import com.shramov.currencyexchanger.utils.Const

data class Currency(
    val base  : String?,
    val date  : String,
    val rates : Map<String, Double>,
    val commissionFee: Double = Const.DEFAULT_COMMISSION
)