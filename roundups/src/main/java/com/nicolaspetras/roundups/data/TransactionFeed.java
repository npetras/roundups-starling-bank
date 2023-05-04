package com.nicolaspetras.roundups.data;

import lombok.Data;

import java.util.ArrayList;

@Data
public class TransactionFeed {
    private ArrayList<Transaction> transactions;
}
