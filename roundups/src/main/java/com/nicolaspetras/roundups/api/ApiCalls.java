package com.nicolaspetras.roundups.api;

import com.google.gson.Gson;
import com.nicolaspetras.roundups.data.request.SavingGoalRequestBody;
import com.nicolaspetras.roundups.data.request.TopUpRequestBody;
import com.nicolaspetras.roundups.data.response.error.ErrorDetail;
import com.nicolaspetras.roundups.data.response.error.ErrorResponse;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Handles all the API calls to the Startling Bank API for the application.
 */
public class ApiCalls {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    public static final String STARLING_API_BASE_URL = "https://api-sandbox.starlingbank.com";
    private static final String ACCESS_TOKEN = "Bearer eyJhbGciOiJQUzI1NiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAA_21Ty5KjMAz8lS3Oo6lAeITc9rY_sB8gJDlxDdiUbTI7tbX_vgaTEFJzo7ullmSJv5n2PjtnOGpgGey7D-h6bS4dmo93skP2lvmpixF5XqGcThUcajxAeTp20BIpqPBQsLR1zUQxWP6M2TmvT8emrA5l85ZpDImoq1M9E0hkJxN-2Z7F_da8eqtGEQi2JZTclYA151BTl1dV1Vaq6aJ3sB9iUkbBx7xi1cGxzRWUhAQdqgK67oRt3lLb5G3MiGP9JBLvtzpHrAvIa1VDqaiEtqjbOFJzxEKo5CKfByY7yvwoqVO4Lq2CwUHOTpB_vAjha3wRNIsJWmlxe77XPuyYFTC72ORZWIcHSEoISNdBHpFTuFqnfdwQaMP6pnnCPmkd9mho7YTQMZA1wdk--c7MqlmjtBswaGvAKlCT4bUeTT7Y4d62DKjX7AENY5AzSy9BHnAJGyRgRHimCGfxjpfMEb9E7lICq0kCWxDoAS-rZ9K2TwgOjUeae37Q0FuK02_eiQA7P8Mru2Y5q3R_L5Vq76glygmJHsMO-L306XR093iLq_BwsVsfO24ddcctPs9MGk7FZ__GYhO_8drEZEpX4akXhjj2djVeQogDTuMKR7yfSfzd4xXFY7KOn8rv2XvdPftNPthP8-CDzA0A-dsrNbJK1PNOl1W8Ljn79x9it8BEoQQAAA.KMmwE2QsQJm7gNIFvwQMIxlsR7hmXyZImnOtSR8QZlArRnQyz0MoClGTeu4cs0OcKUhRTBhof-axMK3IhuaZUTWdZN0xsdswRsmxTZlwJ5-9XGAf1nH_u2VyXUGfAatuVpH1qCrVe51qhWKJCdG24tkTVfcUhgL7jT2Q1RBF8zXcPiR2Yge6YQM3jJrIwPidaUjDHgYRBw6ppAvywKLuBVoucfh2Si0vhDcUA0eu3F4iHTKYsGiVOmKIRSPItEhwqN4c1gW1YXy8kIHtO3WrNXqLJi5aaMP92Q4SQ6FtSLYgg6o9T9PxOGP03qw4KnTV2xDcjCiE4_L5UPuQMdwqeiuNCYPU3IQN4r4fKEmjJMBxoA_qshONC129p-9ZjeDzKkOl3VBYTCHAe4TwCeDmwYgsd-69mDR_hNDRBCOSyUdnWsKhTLkT20qodAnWKEarEej_t5OYiVEwI4wuenJQf801LNdD2e2HtqVR_Sb8eD-VwSTf2wvrU2iFGG7RWyvxtlU1_RPvd_MgRuwSaNtLE9WNgsKCJVJWwCWGzGY3XnUAD8DU7Z8PAsG6fwG3ZPrknaUByMdpIUwoOgA_ybqnG-B9B3UFrvVnpUHpXgByKHuLLJlNoJZfzCskz7Ll910J_g0Z1Lm8lXUXeK8SYk5kk5FZ2k_UKkXKeUrLY9TgYUI";

    private final HttpClient client;
    private final Gson gson;

    public ApiCalls() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    /**
     * Sends GET request to retrieve data like Accounts or a Transaction Feed. The request is made using the URI
     * provided.
     * @param uri The URI to use to make the HTTP request (API call)
     * @return The body of the response received
     */
    public String sendBasicGetRequest(String uri) {
        var request = HttpRequest.newBuilder(URI.create(uri))
                .header("accept", "application/json")
                .header("authorization", ACCESS_TOKEN)
                .GET()
                .build();
        return sendRequest(request);
    }

    /**
     * Sends a request to create a Savings Goal
     * @param accountUid Unique ID of the account that the savings goal should be created under
     * @param requestBody Provides the data needed to create the Savings Goal as part of the PUT request's body,
     *                    include data like the name of the Savings Goal
     * @return The response body in the form of a String
     */
    public String sendCreateSavingsGoalRequest(String accountUid, SavingGoalRequestBody requestBody) {
        var uri = STARLING_API_BASE_URL + "/api/v2/account/" + accountUid + "/savings-goals";
        var request = HttpRequest.newBuilder(URI.create(uri))
                .header("accept", "application/json")
                .header("authorization", ACCESS_TOKEN)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(this.gson.toJson(requestBody)))
                .build();
        log.info("Sending a Create Savings Goal Request...");
        return this.sendRequest(request);

    }

    /**
     * Sends a request to add money to a Savings Goal
     * @param accountUid Unique ID of the Account, which has the Savings Goal
     * @param savingsGoalUid Unique ID of the Savings Goal where Money is being added
     * @param requestBody The amount and currency to be added to the Savings Goal -- will be sent as part of the
     *                    PUT request's body
     */
    public void sendAddMoneyIntoSavingsGoalRequest(String accountUid, String savingsGoalUid,
                                                   TopUpRequestBody requestBody) {
        var transferUid = generateUid();
        log.info("transferId: " + transferUid);
        var uri = STARLING_API_BASE_URL + "/api/v2/account/" + accountUid + "/savings-goals/"
                + savingsGoalUid +  "/add-money/" + transferUid;

        var request = HttpRequest.newBuilder(URI.create(uri))
                .header("accept", "application/json")
                .header("authorization", ACCESS_TOKEN)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(this.gson.toJson(requestBody)))
                .build();
        log.info("Sending an Add Money into Savings Goal Request...");
        this.sendRequest(request);
    }

    /**
     * Sends the provided request
     * @param request HttpRequest, which includes all necessary data include URI where it should be sent
     * @return The body of the received response
     */
    private String sendRequest(HttpRequest request) {
        log.info("Request details: " + request.toString());
        var responseBody = "";
        try {
            var response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Response received: " + String.valueOf(response));
            responseBody = response.body();
            log.debug("Response body: " + responseBody);

            if (this.isErrorResponse(responseBody)) {
                var errorResponse = gson.fromJson(responseBody, ErrorResponse.class);
                log.error("Following errors received in response: ");
                for(ErrorDetail error : errorResponse.errors()) {
                    log.error(error.message());
                }
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            log.error("Provided an invalid request parameter to the HttpClient: "
                    + illegalArgumentException.getLocalizedMessage());
        }
        catch (IOException ioException) {
            log.error("IO Error: Unable to send request to/receive response from the Starling Bank API: "
                    + ioException.getLocalizedMessage());
        } catch (InterruptedException interruptedException) {
            log.error("Interrupted while making a call to the Starling Bank API: "
                    + interruptedException.getLocalizedMessage());
        }
        catch (SecurityException securityException) {
            log.error("Denied access by Security Manager" + securityException.getLocalizedMessage());
        }
        catch (Exception e) {
            log.error("Unknown error occurred during a call to the Starling Bank API: " + e.getLocalizedMessage());
        }

        return responseBody;
    }

    /**
     * Checks where the responseBody provided indicates that this is an error response
     * @param responseBody The body of the response received
     * @return true if it contains errors text, else false
     */
    private boolean isErrorResponse(String responseBody) {
        return responseBody.contains("errors");
    }

    /**
     * @return a unique Hexadecimal ID in the same format as the Starling Bank API uses
     */
    private String generateUid() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange(new char[]{'a', 'e'}, new char[]{'0', '9'})
                .build();

        return generator.generate(8) + "-"
                + generator.generate(4) + "-"
                + generator.generate(4) + "-"
                + generator.generate(4) + "-"
                + generator.generate(12);
    }

}
