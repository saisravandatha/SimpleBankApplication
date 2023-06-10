package com.sravan.bank.service;

import com.sravan.bank.dto.BankResponse;
import com.sravan.bank.dto.CreditDebitRequest;
import com.sravan.bank.dto.EnquiryRequest;
import com.sravan.bank.dto.UserRequest;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);

    BankResponse balanceEnquiry(EnquiryRequest request);

    String nameEnquiry(EnquiryRequest request);

    BankResponse creditAccount(CreditDebitRequest request);

    BankResponse debitAccount(CreditDebitRequest request);
}
