package com.nicolaspetras.roundups.data.request;

import com.nicolaspetras.roundups.data.CurrencyAndAmount;

/**
 * Request body used when adding money to a Savings Goal
 * @param amount
 */
public record TopUpRequestBody (CurrencyAndAmount amount) {
}
