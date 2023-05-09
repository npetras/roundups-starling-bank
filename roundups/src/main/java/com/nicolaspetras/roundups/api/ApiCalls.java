package com.nicolaspetras.roundups.api;

import com.google.gson.Gson;
import com.nicolaspetras.roundups.data.request.SavingGoalRequestBody;
import com.nicolaspetras.roundups.data.request.TopUpRequestBody;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.nicolaspetras.roundups.service.RoundupsService.ACCESS_TOKEN;

/**
 * Handles all the API calls to the Startling Bank API for the application.
 */
public class ApiCalls {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    public static final String STARLING_API_BASE_URL = "https://api-sandbox.starlingbank.com";

    private final HttpClient client;
    private final Gson gson;

    public ApiCalls() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    /**
     * Sends a request using the URI provided and returns the resulting response, used to make GET requests to
     * retrieve data like Accounts or a Transaction Feed.
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

    public String sendAddMoneyIntoSavingsGoalRequest(String accountUid, String savingsGoalUid, TopUpRequestBody requestBody) {
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
        return this.sendRequest(request);
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
