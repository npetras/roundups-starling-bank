package com.nicolaspetras.roundups.data.savings;

/**
 * Represents a list of Starling Bank Savings Goals.
 * Used to represent the data that is returned when making a Get Savings Goals request, which returns a list of all
 * savings goals for a particular account.
 * @param savingsGoalList
 */
public record SavingsGoalList(SavingsGoal[] savingsGoalList) {
    /**
     * Checks if a particular Savings Goal is present in the savingsGoalList
     * @param savingsGoalName The name of the Savings Goal that caller wants to check for
     * @return true if a savings goal with the same name as the savingsGoalName parameter exist in the savingsGoalList
     */
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

    /**
     * Tries to find and return a specific savings goal, using the Savings Goal name as the search paramater
     * @param savingsGoalName The name of the saving goal you want to retrieve
     * @return The SavingsGoal object that has the 'savingsGoalName' provided, or null if the Savings Goal is not found
     */
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
