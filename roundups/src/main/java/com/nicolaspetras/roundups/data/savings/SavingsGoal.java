package com.nicolaspetras.roundups.data.savings;

import com.nicolaspetras.roundups.data.CurrencyAndAmount;

/**
 * Represents a Starling Bank Savings Goal
 * @param savingsGoalUid
 * @param name
 * @param target
 * @param totalSaved
 * @param savedPercentage
 */
public record SavingsGoal(String savingsGoalUid, String name, CurrencyAndAmount target, CurrencyAndAmount totalSaved,
                          int savedPercentage) {
}
