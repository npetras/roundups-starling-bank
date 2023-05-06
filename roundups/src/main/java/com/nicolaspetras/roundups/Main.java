package com.nicolaspetras.roundups;

import com.google.gson.Gson;
import com.nicolaspetras.roundups.api.ApiCalls;
import com.nicolaspetras.roundups.data.Accounts;
import com.nicolaspetras.roundups.data.Transaction;
import com.nicolaspetras.roundups.data.TransactionFeed;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.time.LocalDateTime;


public class Main {
    public static final String ACCESS_TOKEN = "Bearer eyJhbGciOiJQUzI1NiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAA_21Ty47bMAz8lcLn5SJ-KXZuvfUH-gEURSXC2pIhydkuiv575ciJ42BvnhlySIr038KEUJwKnAwoHt17iOgHY88S7cc7ubF4K8IsU0RZtshd18JB4AGarpbQE2lo8VAp7oVQRCmY_0zFqRRd3RzrQ1W9FQZjJupOiIVAIjfb-MsNiv1vo1ZvfdQEjH0DjZINoFAlCJJl27Z9q48yeUf3wXbNkEfJQh6gJUoZ2HcgKyFBdChV3VWHtitTRhrrJxGHsNWpUVRQCi2g0Sm1r0SfRjrWWDE1qlqyArmJl0fJncLl1ipYHPnkGdWPFyF-TS-CUWyj0Yb9nh9MiDtmBUr51OSJlYkPkJUYkS4jPyLneHHehLQhMFaZq1EzDlmTOKCltRNCr4Ccjd4N2XdhVs1ZbfyI0TgLToOerVrr0RyiG-9t84hmzR7RKox8Ujxw5Ae8hY0cMSE8UYKLeMe3zAm_mO9SBqtJBlsQmBHPq2fWtk-IHm1AWnp-0DA4StNv3pkAtzzDK7tmeafNcC-Va--oW5RnYjPFHQh76dOb5B7wmlYR4Oy2PnbcOuqOu_k8M3k4nZ79G4tN_MZrE7MpXVjNAytIY29XEzjGNOA8rXDC-5mk3z1dUTom59VT-T17r7tnv8kH92kffOSlAaBwfaUmpTP1vNPbKl6XXPz7D5so362hBAAA.NUNeywYpGInZA2iPFulaWFWHobSA6NkWP3Qvtdz6_VK-6cFCIIHEhroCGg6qeUuj81QssWqCW8Z8bs6PUC7cP0QCiwe1NUOj2KpeZ--f3Wi3WdkS2SDu4eVtVRW1I5jsmOvpwnkdS3tLDnUvA0AbYYvy6QCo4YLnJvO_krUTgvZ_L_ZBadDuD2TQHdM-F0giCaiZLrG7k09x2Uh_MPbBgGS5W85TTeLYfIgBjqOuFqGpdW2zEPyxK5LwR_oup-Lg3zRYGbfkOXl3NuDF_GQnTnudOKeRZdBJBnQhHHzIxpPJSqc0Za7_pNpairUJL2ctKTpSu57DeBqt5Fss6--_vjBBK8taJVIPAVBlY6IOiIAyQ2AvAu3_oNwt5_0O8Q90PhjXgQZTZPVqyMK4kx4HmK9DmEB17OzYeOke0zi4G4DrLFBgnyuZs28E6YmF-H4ryq8mO5PV5wzkOW6WP31A7q31MWNcZ7kGidexRwcTmBeE63R0TQWfRxo4oHVwz4CAQe68NIVJhiBI43OuqDi0Y9LrRFhobHHM3rTnOsf0MQSnQvL4rgAqQcDFDDc2_6_sOBx0CbQLJnEb-PsH4NOfS1QCtwVT0C4fQGqS3bAKkFaJnnkWFYvTAU2Q5eu95EsbBOrkevk_yg-S55C3Ep36KKE8tw3zt-X0nQWWgjh3nnQ";

    public static void main(String[] args) {

        var lastWeekTransactions = retrieveAndDeserialiseLastWeekTransactions();
        var totalRoundUps = calculateTotalRoundups(lastWeekTransactions);

        // move the calculated amount from (current) account to RoundUps Saving Goal

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

        var accountsApiResponse = ApiCalls.retrieve("https://api-sandbox.starlingbank.com/api/v2/accounts");
        Accounts accounts = gson.fromJson(accountsApiResponse, Accounts.class);

        var transactionFeedsUri = "https://api-sandbox.starlingbank.com/api/v2/feed/account/"
                + accounts.accounts()[0].accountUid() + "/category/" + accounts.accounts()[0].defaultCategory()
                + "?changesSince=2020-01-01T12%3A34%3A56.000Z";
        var transactionFeedsResponse = ApiCalls.retrieve(transactionFeedsUri);

        System.out.println(LocalDateTime.now().minusWeeks(1).toString());
        System.out.println(transactionFeedsResponse);

        return gson.fromJson(transactionFeedsResponse, TransactionFeed.class);
    }

    /**
     * Each transaction is rounded up to the closest main unit of the currency (100 minor units), if it isn't
     * already a multiple of 100. All world currency are decimal based, except 2, but these non-decimal
     * sub-units are not used.
     * @param transactionFeed Feed (list) of transactions
     * @return the total of the round ups on the transactions provided.
     */
    private static int calculateTotalRoundups(TransactionFeed transactionFeed) {
        var totalRoundup = 0;
        for (Transaction transaction : transactionFeed.feedItems()) {
            System.out.println(transaction.amount().minorUnits());
            var roundUpCalculation = 100 - (transaction.amount().minorUnits() % 100);
            if (roundUpCalculation < 100) {
                totalRoundup += roundUpCalculation;
                System.out.println("Round-up: " + roundUpCalculation);
            } else {
                System.out.println("No round-up");
            }
        }
        System.out.println("Total round-up: " + totalRoundup);
        return totalRoundup;
    }
}