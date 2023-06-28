package com.sravan.bank.service;

import com.sravan.bank.dto.TransactionDto;
import com.sravan.bank.entity.Transaction;
import com.sravan.bank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService{

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .amount(transactionDto.getAmount())
                .accountNumber(transactionDto.getAccountNumber())
                .status(transactionDto.getStatus())
                .build();
        transactionRepository.save(transaction);
    }
}
