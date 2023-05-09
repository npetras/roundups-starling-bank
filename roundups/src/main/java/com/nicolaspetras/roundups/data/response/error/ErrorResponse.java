package com.nicolaspetras.roundups.data.response.error;

/**
 * Represent the ErrorResponse that can be returned in the body of a response from the Starling Bank API
 * @param errors Array of errors that occurred
 * @param success Whether the request was successful
 */
public record ErrorResponse(ErrorDetail[] errors, boolean success) {
}
