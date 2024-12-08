package com.example.lesalonproject;



import com.oanda.v20.account.AccountID;

public class Configuration {
    private Configuration() {}

    public static final String URL = "https://api-fxpractice.oanda.com";
    public static final String TOKEN = "5f5036f59934645111ab53c85b6149d9-44e5618b7dd38fe118681d87b77717d7";
    @SuppressWarnings("exports")
    public static final AccountID ACCOUNTID = new AccountID("101-004-30416899-001");
}

