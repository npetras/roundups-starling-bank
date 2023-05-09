package com.nicolaspetras.roundups.data.transaction;

import com.nicolaspetras.roundups.data.CurrencyAndAmount;

/**
 * Represents a Starling Bank transaction, it does not include all the fields returned from the API,
 * because only a few are needed, even the current list is not used, but seemed to be the most useful data
 * that might be relevant now and in the future.
 *
 * @param feedItemUid Unique ID of the Transaction (Feed Item)
 * @param categoryUid The unique ID of the category that this transaction falls under
 * @param amount The amount transferred in this transaction (the currency and amount in minor units)
 * @param sourceAmount The amount for this transaction at the source
 * @param direction Describes whether the transaction is an incoming (IN) or outgoing one (OUT)
 * @param updatedAt Last time the transaction was updated
 * @param transactionTime The time when the transaction occurred
 * @param settlementTime The time when the transaction was settled
 * @param source The method used to execute the transaction, e.g. MASTER_CARD, FASTER_PAYMENTS_OUT
 * @param status The status of the transaction e.g. PENDING, SETTLED
 * @param transactingApplicationUserUid The Application user that made the transaction
 */
public record Transaction(String feedItemUid, String categoryUid, CurrencyAndAmount amount,
                          CurrencyAndAmount sourceAmount, String direction, String updatedAt, String transactionTime,
                          String settlementTime, String source, String status, String transactingApplicationUserUid) {

}
