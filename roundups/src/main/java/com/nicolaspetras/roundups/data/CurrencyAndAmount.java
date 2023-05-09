package com.nicolaspetras.roundups.data;

/**
 * Represents an amount of money in terms of the currency and the amount in minor units
 * @param currency The type of currency e.g. GBP, EUR
 * @param minorUnits amount in minor units of the currency e.g. pence for GBP, cents for EUR
 */
public record CurrencyAndAmount(String currency, int minorUnits) {
}
