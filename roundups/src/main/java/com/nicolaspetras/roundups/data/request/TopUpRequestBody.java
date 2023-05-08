package com.nicolaspetras.roundups.data.request;

import com.nicolaspetras.roundups.data.CurrencyAndAmount;

public record TopUpRequestBody (CurrencyAndAmount amount) {
}
