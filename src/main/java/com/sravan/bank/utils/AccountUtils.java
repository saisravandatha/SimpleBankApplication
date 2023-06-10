package com.sravan.bank.utils;

import java.time.Year;

public class AccountUtils {

    public static String generateAccountNumber(){
        //Account Number in the format of CurrentYear + random 6 DigitNumber
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;
        //generate a random number between min and max
        int randomNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);
        StringBuilder accountNumber = new StringBuilder(String.valueOf(currentYear).concat(String.valueOf(randomNumber)));
        return accountNumber.toString();
    }

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "User has already an account created...";

    public static final String ACCOUNT_CREATE_CODE = "002";
    public static final String ACCOUNT_CREATE_MESSAGE = "Account has successfully created!!!";

    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = " User with the provided Account Number does not exist. Please provide Valid Accunt Number";

    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_SUCCESS = "Account Found Successfully!!!";

    public static final String ACCOUNT_CREDITED_SUCCESS_CODE = "005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "Amount Successfully Credited to your account!!!";

    public static final String ACCOUNT_DEBITED_SUCCESS_CODE = "007";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "Amount has been successfully debited to your account!!!";

    public static final String INSUFFICIENT_BALANCE_CODE = "006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient Balance...";



}
