package com.nicolaspetras.roundups.data.response;

/**
 * Used to represent the data returned in the response body, after after making a Create Savings Goal request
 * @param savingsGoalUid
 * @param success
 */
public record CreateOrUpdateSavingsGoalResponse(String savingsGoalUid, boolean success) {
}
