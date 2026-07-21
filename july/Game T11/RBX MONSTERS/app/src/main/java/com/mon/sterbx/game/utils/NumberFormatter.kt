package com.mon.sterbx.game.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object NumberFormatter {

    private val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ' '   // пробіл замість коми
    }

    private val formatter = DecimalFormat("#,###", symbols).apply {
        isGroupingUsed = true
    }

    fun format(value: Long): String = formatter.format(value)
    fun format(value: Int): String  = formatter.format(value)

}