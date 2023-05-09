package com.nicolaspetras.roundups.data.transaction;

import java.util.List;

/**
 * List or Feed of Starling Bank Transactions
 * @param feedItems The list of transactions.
 */
public record TransactionFeed(List<Transaction> feedItems) {
}
