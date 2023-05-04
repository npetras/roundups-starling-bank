package com.nicolaspetras.roundups.data;

import lombok.Data;



/**
 *
 */
@Data
public class Transaction {
    private String uniqueId;    // unique identifier for the transaction
    private Amount amount;
    @Data
    private class Amount {
        // ISO-4217 3 character currency code e.g. GBP or EUR
        private String currency;
        // amount of money in the most basic (minor) unit for said currency e.g. pence for GBP or cent for EUR
        private int minorUnits;
    }
}
