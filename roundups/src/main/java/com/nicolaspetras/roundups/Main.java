package com.nicolaspetras.roundups;

import com.google.gson.Gson;
import com.nicolaspetras.roundups.api.ApiCalls;
import com.nicolaspetras.roundups.data.CurrencyAndAmount;
import com.nicolaspetras.roundups.data.account.AccountsList;
import com.nicolaspetras.roundups.data.request.SavingGoalRequestBody;
import com.nicolaspetras.roundups.data.request.TopUpRequestBody;
import com.nicolaspetras.roundups.data.response.CreateOrUpdateSavingsGoalResponse;
import com.nicolaspetras.roundups.data.savings.SavingsGoalList;
import com.nicolaspetras.roundups.data.transaction.Transaction;
import com.nicolaspetras.roundups.data.transaction.TransactionFeed;

import com.nicolaspetras.roundups.service.RoundupsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.nicolaspetras.roundups.api.ApiCalls.STARLING_API_BASE_URL;


public class Main {
    static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        RoundupsService appService = new RoundupsService();
        appService.runRoundupsApplication();
    }

    /**
     * Retrieves a feed of the last week of transactions from the Starling Bank API using the account
     * data (account UID and default category ID) also retrieved from the Starling Bank API. The Starling API
     * returns the data in the form of JSON, which is deserialised into the application's Java classes using the
     * GSON library.
     *
     * @return Transaction Feed of last week's transaction
     */
    private static TransactionFeed retrieveAndDeserialiseLastWeekTransactions() {
        var gson = new Gson();
        var apiCalls = new ApiCalls();

        var accountsApiResponse = apiCalls.sendBasicGetRequest("https://api-sandbox.starlingbank.com/api/v2/accounts");
        log.debug(accountsApiResponse);

        AccountsList accounts = gson.fromJson(accountsApiResponse, AccountsList.class);

        var transactionFeedsUri = "https://api-sandbox.starlingbank.com/api/v2/feed/account/"
                + accounts.accounts()[0].accountUid() + "/category/" + accounts.accounts()[0].defaultCategory()
                + "?changesSince=2020-01-01T12%3A34%3A56.000Z";
        var transactionFeedsResponse = apiCalls.sendBasicGetRequest(transactionFeedsUri);
        log.debug(transactionFeedsResponse);

        return gson.fromJson(transactionFeedsResponse, TransactionFeed.class);
    }
}