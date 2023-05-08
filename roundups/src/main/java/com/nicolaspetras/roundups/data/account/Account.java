package com.nicolaspetras.roundups.data.account;

public record Account(String accountUid, String accountType, String defaultCategory, String currency,
                      String createdAt, String name) {


}