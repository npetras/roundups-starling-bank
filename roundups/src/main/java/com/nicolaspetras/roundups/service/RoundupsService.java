package com.nicolaspetras.roundups.service;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.nicolaspetras.roundups.api.ApiCalls.STARLING_API_BASE_URL;

public class RoundupsService {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public static final String ACCESS_TOKEN = "Bearer eyJhbGciOiJQUzI1NiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAA_21Ty27cMAz8lcLnMIhfWntvvfUH-gE0SWWF2JIhyUmDov9eeeVdx4vcPDPkkBTpv4UJoTgXOBtgmdxziOhHY18HtG_P5KbiqQjLkCLKskXpuhZeFL5A09UD9EQaWnypWHqlmCgFy5-5OJeqq1XddXX7VBiMmWjbqlkJJHKLjb_cyOJ_G9689UkTCPYNNDw0gIpLUDSUbdv2rT4NyTu6N7E5o1LNqa8rgXJQChpihKHWHXSc2BL1cJIqZaSxfhJJCHudGlUFpdIpS1MDfaX6NNKpxkqo4apcByY3y_oouVO4XFsFi5OcvSD_eBDi5_wgGBYbjTbij_xoQjwwG2D2qcmzsIl3kJUYkS6T3COXeHHehLQhMJbNu-EFx6wNOKKlrRNCz0DORu_G7Lsym-asNn7CaJwFp0Evlrd6tIToplvbMqHZsie0jFHOLKNEucNr2CQRE8IzJbiKN3zNnPFT5CZlsJlksAeBmfB188za_gnRow1Ia893GkZHafrdOxPg1md4ZLcs77QZb6Vy7QN1jfJCYuZ4AOEofXiT3AO-p1UEeHV7HwduG_XAXX2-Mnk4nZ79G4td_MZrF7MpXYSXURjS2PvVBIkxDbjMG5zxdibpd09XlI7Jef5S_sje6h7Zb_LBfdg7H2VtACi8P1Iz60x93el1FY9LLv79BwNQQVGhBAAA.EE8Y9FapdFUlFbDDy-u1s9vUaw0F85ocpQ3CIHxrMDC1Mj4sKfiVD026miHEyaohBBTiu19xkt78ZieiKsyafeLoUL0ZndkfHas-V3HDu89wOyGAJeKZ6VQ-3I4E68TJpxuGhcqpFrf6ibcT8Sq_ukiAMcQYvTx3b_coJ7jphErWqob1hVmc9P5rTPbwRwy76o7henSbguEEjyk1HltrrdC4a7JNXy0KFhc5iMLhr-bm6JOeH5DiBpMXFfEPYCBbuSbqA79ABgZDswETcY_LBl4pWy8mow2Ll3ceQN1jRHwZlQxHtWirc0WofyHcj5iAH7GNYBetI_AbwAODtrmEdESx4SSZ2JhbmVnVGVx706imIDai2uv6BDpYdYaZIaQqmOmezZT_sjOSNnLcNESMRRs8AUpyRqXjnTxhtKvQC3p3Otd3GpnXnvPAcbwd3F7hjW7mDHIXcUXj5SvPFmW8qodeF_sshIEEKIGpODqeu3VBaTUPhokKp68CIXwO2pHF4Gd_b4DZ-8P2Mh1hHQ_r-eHRRK_rktNVCPutqRRT4jcQQM5FiYyMC65SlcDWOzV4CkZOXLw2K5i6mByYjmyExvFMrOAhOyChaf53Jrb1Fx1jDVX_xDyVVPAuarRQRHM5UAEC_H1WZa2GtU-P2GvPcjHjC0VbYkpp4BXrJmmNeUo";
    public static final String ROUND_UP_SAVINGS_GOAL = "Round-up Savings";
    private Gson gson;
    private ApiCalls apiCalls;

    public RoundupsService() {
        this.gson = new Gson();
        this.apiCalls = new ApiCalls();
    }

    public void runRoundupsApplication() {
        var accountsApiResponse = apiCalls.sendBasicGetRequest(STARLING_API_BASE_URL + "/api/v2/accounts");
        log.debug(accountsApiResponse);

        AccountsList accounts = gson.fromJson(accountsApiResponse, AccountsList.class);

        var transactionFeedsUri = STARLING_API_BASE_URL + "/api/v2/feed/account/"
                + accounts.accounts()[0].accountUid() + "/category/" + accounts.accounts()[0].defaultCategory()
                + "?changesSince=2020-01-01T12%3A34%3A56.000Z";
        var transactionFeedsResponse = apiCalls.sendBasicGetRequest(transactionFeedsUri);
        log.debug(transactionFeedsResponse);

        var lastWeekTransactions = gson.fromJson(transactionFeedsResponse, TransactionFeed.class);
        var totalRoundup = calculateTotalRoundup(lastWeekTransactions);
        System.out.println("Total round-up: " + totalRoundup);
        var savingGoalsResponse = apiCalls.sendBasicGetRequest(STARLING_API_BASE_URL + "/api/v2/account/" + accounts.accounts()[0].accountUid() + "/savings-goals");
        var savingsGoalList = gson.fromJson(savingGoalsResponse, SavingsGoalList.class);

        if (totalRoundup > 0) {
            // if the savings goal already exists
            if (savingsGoalList.isSavingsGoalPresent(ROUND_UP_SAVINGS_GOAL)) {
                var roundUpsSavingGoal = savingsGoalList.getSavingGoal(ROUND_UP_SAVINGS_GOAL);
                var requestBody = new TopUpRequestBody(new CurrencyAndAmount("GBP", totalRoundup));
                var addMoneyToSavingsGoalResponse = apiCalls.sendAddMoneyIntoSavingsGoalRequest(accounts.accounts()[0].accountUid(),
                        roundUpsSavingGoal.savingsGoalUid(), requestBody);
                log.debug("Add money to saving goal response: " + addMoneyToSavingsGoalResponse);
            } else {
                // create savings goal
                var savingsGoalRequestBody = new SavingGoalRequestBody(ROUND_UP_SAVINGS_GOAL, "GBP",
                        new CurrencyAndAmount("GBP", 100000), "string");
                var createSavingsGoalResponseJson =
                        apiCalls.sendCreateSavingsGoalRequest(accounts.accounts()[0].accountUid(), savingsGoalRequestBody);
                var createSavingsGoalResponse = gson.fromJson(createSavingsGoalResponseJson,
                        CreateOrUpdateSavingsGoalResponse.class);
                var topUpRequestBody = new TopUpRequestBody(new CurrencyAndAmount("GBP", totalRoundup));

                var addMoneyToSavingsGoalResponse = apiCalls.sendAddMoneyIntoSavingsGoalRequest(accounts.accounts()[0].accountUid(),
                        createSavingsGoalResponse.savingsGoalUid(), topUpRequestBody);
                System.out.println("Added " + totalRoundup + "to the Round-ups Savings Goal");
//                System.out.println("New balance for the savings goal: " + sav);
                log.debug("Add money to saving goal response: " + addMoneyToSavingsGoalResponse);
            }
        } else {
            System.out.println("There was no round-ups to transfer to the Savings Goal");
        }
    }

    /**
     * Each transaction is rounded up to the closest main unit of the currency (100 minor units), if it isn't
     * already a multiple of 100. All world currency are decimal based, except 2, but these non-decimal
     * minor units are not used so the round up calculation should work for all of them.
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
