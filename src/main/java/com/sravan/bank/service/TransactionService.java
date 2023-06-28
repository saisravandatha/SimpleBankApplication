package com.sravan.bank.service;

import com.sravan.bank.dto.TransactionDto;
import com.sravan.bank.entity.Transaction;

public interface TransactionService {

    void saveTransaction(TransactionDto transaction);
}
