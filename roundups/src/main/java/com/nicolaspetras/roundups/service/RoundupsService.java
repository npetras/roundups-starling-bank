package com.nicolaspetras.roundups.service;

import com.google.gson.Gson;
import com.nicolaspetras.roundups.api.ApiCalls;
import com.nicolaspetras.roundups.data.CurrencyAndAmount;
import com.nicolaspetras.roundups.data.account.Account;
import com.nicolaspetras.roundups.data.account.AccountsList;
import com.nicolaspetras.roundups.data.request.SavingGoalRequestBody;
import com.nicolaspetras.roundups.data.request.TopUpRequestBody;
import com.nicolaspetras.roundups.data.response.CreateOrUpdateSavingsGoalResponse;
import com.nicolaspetras.roundups.data.savings.SavingsGoalList;
import com.nicolaspetras.roundups.data.transaction.Transaction;
import com.nicolaspetras.roundups.data.transaction.TransactionFeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static com.nicolaspetras.roundups.api.ApiCalls.STARLING_API_BASE_URL;

/**
 * Application class for the Round-ups Service, which GETs the relevant data from the Starling API on the
 * customer Account and Transaction Feed, and uses that information to calculate round-ups on last week's transactions
 * and then move the total round-up amount to a Round Up Savings Goal.
 */
public class RoundupsService {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public static final String ROUND_UP_SAVINGS_GOAL = "Round-up Savings";
    private final Gson gson;
    private final ApiCalls apiCalls;

    public RoundupsService() {
        this.gson = new Gson();
        this.apiCalls = new ApiCalls();
    }

    /**
     * Runs the Round-Ups application.
     */
    public void runRoundupsApplication() {
        Account primaryAccount = retrievePrimaryAccount();
        var lastWeekTransactions = retrieveLastWeekTransactionFeed(primaryAccount.accountUid(),
                primaryAccount.defaultCategory());

        var totalRoundup = calculateTotalRoundup(lastWeekTransactions);
        System.out.println("Total round-up: " + totalRoundup);

        var savingsGoalList = retrieveSavingsGoalList(primaryAccount.accountUid());

        if (totalRoundup > 0) {
            // if the savings goal already exists
            if (savingsGoalList.isSavingsGoalPresent(ROUND_UP_SAVINGS_GOAL)) {
                var roundUpsSavingGoal = savingsGoalList.getSavingGoal(ROUND_UP_SAVINGS_GOAL);
                addRoundupAmountToSavingsGoal(primaryAccount.accountUid(), totalRoundup,
                        roundUpsSavingGoal.savingsGoalUid());
            } else {
                // create savings goal
                var createSavingsGoalResponse = createSavingsGoal(primaryAccount.accountUid());
                addRoundupAmountToSavingsGoal(primaryAccount.accountUid(), totalRoundup,
                        createSavingsGoalResponse.savingsGoalUid());
            }
            System.out.println("Added " + totalRoundup + " to the " + ROUND_UP_SAVINGS_GOAL + " Goal");
        } else {
            System.out.println("There was no round-ups to transfer to the Savings Goal");
        }
    }

    /**
     * Sends a GET request to the Starling Bank API to retrieve the list of accounts for our sandbox customer,
     * coverts the JSON response received from the Starling Bank API to an AccountList object, and then finds the
     * primary account in the list.
     *
     * @return The customer's primary account
     */
    private Account retrievePrimaryAccount() {
        var accountsApiResponse = apiCalls.sendBasicGetRequest(STARLING_API_BASE_URL + "/api/v2/accounts");
        var accountsList = gson.fromJson(accountsApiResponse, AccountsList.class);

        Account primaryAccount = null;

        for (Account account : accountsList.accounts()) {
            if (account.accountType().equals("PRIMARY")) {
                primaryAccount = account;
            }
        }
        return primaryAccount;
    }

    /**
     * Sends a GET request to the Starling API to retrieve the transactions since last week for our sandbox customer,
     * using the accountUid and accountDefaultCategory. The JSON response received is converted to a TransactionFeed
     * object.
     *
     * @param accountUid             The unique identifier for the account from where the transactions should be retrieved
     * @param accountDefaultCategory The account's default category, which will be used to retrieve the transaction
     * @return The customer's feed of transactions since last week
     */
    private TransactionFeed retrieveLastWeekTransactionFeed(String accountUid, String accountDefaultCategory) {
        // replicates Starling Bank Format for their Date Time
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        var lastWeek = OffsetDateTime.now().minusWeeks(1);
        var lastWeekStarlingBankFormat = lastWeek.withOffsetSameInstant(ZoneOffset.UTC).format(formatter);

        var transactionFeedsUri = STARLING_API_BASE_URL + "/api/v2/feed/account/"
                + accountUid + "/category/" + accountDefaultCategory
                + "?changesSince=" + lastWeekStarlingBankFormat;
        var transactionFeedsResponse = apiCalls.sendBasicGetRequest(transactionFeedsUri);
        return gson.fromJson(transactionFeedsResponse, TransactionFeed.class);
    }

    /**
     * Each transaction is rounded up to the closest main unit of the currency (100 minor units), if it isn't
     * already a multiple of 100. All world currency are decimal based, except 2, but these non-decimal
     * minor units are not used so the round up calculation should work for all of them.
     *
     * @param transactionFeed Feed (list) of transactions
     * @return the total of the round ups on the transactions provided.
     */
    private int calculateTotalRoundup(TransactionFeed transactionFeed) {
        var totalRoundup = 0;
        for (Transaction transaction : transactionFeed.feedItems()) {
            // only perform round-ups on specific outbound settled transactions
            if (isValidRoundUpTransaction(transaction)) {
                var roundUpCalculation = 100 - (transaction.amount().minorUnits() % 100);
                if (roundUpCalculation < 100) {
                    totalRoundup += roundUpCalculation;
                    log.info("Round-up: " + roundUpCalculation + "from Transaction: " + transaction.feedItemUid());
                } else {
                    log.info("No round-up. Amount: " + transaction.amount().minorUnits()
                            + " Transaction: " + transaction.feedItemUid());
                }
            }
        }
        return totalRoundup;
    }

    /**
     * Sends a GET request to retrieve the list of savings goal for our sandbox customer from the Starling Bank
     * API, and converts the JSON response received to a SavingsGoalList object.
     *
     * @param accountUid The unique identifier of the account from where the savings goals are retrieved
     * @return List of Savings Goals that the customer has
     */
    private SavingsGoalList retrieveSavingsGoalList(String accountUid) {
        var savingGoalsResponse = apiCalls.sendBasicGetRequest(STARLING_API_BASE_URL + "/api/v2/account/"
                + accountUid + "/savings-goals");
        return gson.fromJson(savingGoalsResponse, SavingsGoalList.class);
    }

    /**
     * Add the round-up amount to the savings goal provided, by sending a PUT request to the Starling Bank API.
     *
     * @param accountUid     The unique identifier of the account from where the savings goal is retrieved
     * @param totalRoundup   The total amount from round-ups that should be added to the savings goal
     * @param savingsGoalUid The unique identifier of the savings goal to which the totalRoundup will be added
     */
    private void addRoundupAmountToSavingsGoal(String accountUid, int totalRoundup, String savingsGoalUid) {
        var requestBody = new TopUpRequestBody(new CurrencyAndAmount("GBP", totalRoundup));
        apiCalls.sendAddMoneyIntoSavingsGoalRequest(accountUid, savingsGoalUid, requestBody);
    }

    /**
     * Creates the Rounds-up saving goal, by sending a PUT request to the Starling Bank API.
     *
     * @param accountUid The unique identifier of the account under which the new savings goal will be created
     * @return The response from the PUT request that includes the unique identifier for the savings goal that was
     * created
     */
    private CreateOrUpdateSavingsGoalResponse createSavingsGoal(String accountUid) {
        var savingsGoalRequestBody = new SavingGoalRequestBody(ROUND_UP_SAVINGS_GOAL, "GBP",
                new CurrencyAndAmount("GBP", 100000), "string");
        var createSavingsGoalResponseJson =
                apiCalls.sendCreateSavingsGoalRequest(accountUid, savingsGoalRequestBody);
        return gson.fromJson(createSavingsGoalResponseJson, CreateOrUpdateSavingsGoalResponse.class);
    }

    /**
     * Checks whether the transaction providing is outgoing, settled and a specific type of payment i.e. master card or
     * outbound faster payment.
     *
     * @param transaction The transaction to be checked
     * @return True for outbound, settled transaction from a master card or faster payments out source. Otherwise,
     * False.
     */
    private boolean isValidRoundUpTransaction(Transaction transaction) {
        return transaction.direction().equals("OUT")
                && transaction.status().equals("SETTLED")
                && (transaction.source().equals("FASTER_PAYMENTS_OUT") || transaction.source().equals("MASTER_CARD"));
    }
}
