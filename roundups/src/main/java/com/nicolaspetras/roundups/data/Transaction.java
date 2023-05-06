package com.nicolaspetras.roundups.data;

/**
 * Represents a Starling Bank transaction, it does not include all the fields returned from the API,
 * because only a few are needed, even the current list is not used, but seemed to be the most useful data
 * that might be relevant now and in the future.
 *
 * @param feedItemUid Unique ID of the Transaction (Feed Item)
 * @param categoryUid The unique ID of the category that this transaction falls under
 * @param amount The amount transferred in this transaction (the currency and amount in minor units)
 * @param sourceAmount The amount for this transaction at the source
 * @param direction Describes whether the transaction is an incoming or outgoing one -- money coming in or out
 * @param updatedAt Last time the transaction was updated
 * @param transactionTime The time when the transaction occurred
 * @param settlementTime The time when the transaction was settled
 * @param source How the transaction was executed, e.g. CONTACTLESS, APPLE_PAY, ATM
 * @param status The status of the transaction e.g. PENDING, SETTLED
 * @param transactingApplicationUserUid The Application user that made the transaction
 */
public record Transaction(String feedItemUid, String categoryUid, CurrencyAndAmount amount,
                          CurrencyAndAmount sourceAmount, String direction, String updatedAt, String transactionTime,
                          String settlementTime, String source, String status, String transactingApplicationUserUid) {

}
