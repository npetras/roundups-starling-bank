package com.nicolaspetras.roundups.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public record TransactionFeed(List<Transaction> feedItems) {
}
