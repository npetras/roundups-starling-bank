package com.nicolaspetras.roundups.data.account;

/**
 * A List of Starling Bank Accounts.
 * The Starling Bank API returns a list of all the accounts for a customer.
 * @param accounts A list (array) of accounts
 */
public record AccountsList(Account[] accounts) {
}
