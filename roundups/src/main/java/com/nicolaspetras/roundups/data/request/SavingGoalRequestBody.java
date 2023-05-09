package com.nicolaspetras.roundups.data.request;

import com.nicolaspetras.roundups.data.CurrencyAndAmount;

/**
 * The request body used when creating a new savings goal
 * @param name Name of the new Savings Goal
 * @param currency The currency for the Savings Goal
 * @param target
 * @param base64EncodedPhoto The image to use for the savings goal as a string
 */
public record SavingGoalRequestBody(String name, String currency, CurrencyAndAmount target, String base64EncodedPhoto) {
}
