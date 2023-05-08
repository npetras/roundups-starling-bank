package com.nicolaspetras.roundups.data.request;

import com.nicolaspetras.roundups.data.CurrencyAndAmount;

public record SavingGoalRequestBody(String name, String currency, CurrencyAndAmount target, String base64EncodedPhoto) {
}
