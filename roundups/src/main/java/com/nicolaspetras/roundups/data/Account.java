package com.nicolaspetras.roundups.data;

public record Account(String accountUid, String accountType, String defaultCategory, String currency,
                       String createdAt, String name) {


}