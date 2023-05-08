package com.nicolaspetras.roundups.data.savings;

import com.nicolaspetras.roundups.data.CurrencyAndAmount;

public record SavingsGoal(String savingsGoalUid, String name, CurrencyAndAmount target, CurrencyAndAmount totalSaved,
                          int savedPercentage) {
}
