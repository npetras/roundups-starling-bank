package com.nicolaspetras.roundups.data.account;

/**
 * Represents a single Starling Bank Account
 *
 * @param accountUid unique identifier
 * @param accountType
 * @param defaultCategory The default category for the account, which contains all the main transactions.
 * @param currency
 * @param createdAt
 * @param name
 */
public record Account(String accountUid, String accountType, String defaultCategory, String currency,
                      String createdAt, String name) {


}