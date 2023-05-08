package com.nicolaspetras.roundups.data.savings;

public record SavingsGoalList(SavingsGoal[] savingsGoalList) {
    public boolean isSavingsGoalPresent(String savingsGoalName) {
        boolean result = false;
        for (SavingsGoal savingsGoal : savingsGoalList) {
            if (savingsGoal.name().equals(savingsGoalName)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public SavingsGoal getSavingGoal(String savingsGoalName) {
        SavingsGoal foundSavingsGoal = null;
        for (SavingsGoal savingsGoal : savingsGoalList) {
            if (savingsGoal.name().equals(savingsGoalName)) {
                foundSavingsGoal = savingsGoal;
                break;
            }
        }
        return foundSavingsGoal;
    }
}
