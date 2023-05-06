package com.nicolaspetras.roundups.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.nicolaspetras.roundups.Main.ACCESS_TOKEN;

public class ApiCalls {
    HttpClient client = HttpClient.newHttpClient();

    public static String retrieve(String uri) {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create(uri))
                .header("accept", "application/json")
                .header("authorization", ACCESS_TOKEN)
                .build();

        var responseBody = "";
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);
            responseBody = response.body();
        } catch (Exception ignored) {
            System.err.println("Something went wrong during the API Call");
        }

        return responseBody;
    }
}
