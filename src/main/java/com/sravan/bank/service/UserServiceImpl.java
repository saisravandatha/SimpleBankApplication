package com.sravan.bank.service;

import com.sravan.bank.dto.*;
import com.sravan.bank.entity.User;
import com.sravan.bank.repository.TransactionRepository;
import com.sravan.bank.repository.UserRepository;
import com.sravan.bank.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        //Creating an account - saving a new user into the db
        //Check if user already has an account
        if(userRepository.existsByEmail(userRequest.getEmail())){
            User user = userRepository.findByEmail(userRequest.getEmail());
            return bankResponse(user,AccountUtils.ACCOUNT_EXISTS_CODE,AccountUtils.ACCOUNT_EXISTS_MESSAGE);
        }
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();

        userRepository.save(newUser);

        //Send emailAlerts
        //Used Text Block in setting message body. It is a feature of Java 15
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(userRequest.getEmail())
                .subject("Congratulations!!! Account opened successfully. Welcome to Sarvada Bank..")
                .messageBody(String.format("""
                                 Dear Customer,
                                 
                                 We're delighted that you've joined us! At Sarvada Bank, we understand that your time is precious. So, to speed up your account set up process. we have put together a welcome kit for you. It contains your account details, debit card and cheque book (if signature was uploaded), which you will receive shortly.
                                 
                                 For your reference here are your account details:
                                 
                                 Account Number: %s
                                 Account Name: %s
                                 
                                 Always at your service,
                                 Team Sarvada Bank
                                 """,newUser.getAccountNumber(),newUser.getFirstName()+ " "+newUser.getLastName()))
                .build();
        emailService.sendEmailAlert(emailDetails);
        return  bankResponse(newUser,AccountUtils.ACCOUNT_CREATE_CODE,AccountUtils.ACCOUNT_CREATE_MESSAGE);
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return bankResponse(foundUser,AccountUtils.ACCOUNT_FOUND_CODE,AccountUtils.ACCOUNT_FOUND_SUCCESS);
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExists){
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " "+ foundUser.getLastName() + " " + foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userToCredit.setModifiedAt(LocalDateTime.now());
        userRepository.save(userToCredit);

        //Send emailAlerts After amount got credited
        //Used Text Block in setting message body. It is a feature of Java 15
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(userToCredit.getEmail())
                .subject("Amount Credited to your Account")
                .messageBody(String.format("""
                                 Dear Customer,
                                 
                                 Your A/C %s is credited with INR %s on %s. Your new balance is INR %s. 
                                 
                                 Account Number: %s
                                 Account Name: %s
                                 Current Balance: %s
                                 
                                 Always at your service,
                                 Team Sarvada Bank
                                 """,
                        userToCredit.getAccountNumber(),
                        request.getAmount(),
                        userToCredit.getModifiedAt(),userToCredit.getAccountBalance(),userToCredit.getAccountNumber(),
                        userToCredit.getFirstName()+ " "+userToCredit.getLastName() + " " + userToCredit.getOtherName(),userToCredit.getAccountBalance()))
                .build();
        emailService.sendEmailAlert(emailDetails);

        //Saving Every Transaction in Transactions table
        TransactionDto transactionDto = TransactionDto.builder()
                .transactionType("CREDIT")
                .accountNumber(userToCredit.getAccountNumber())
                .amount(request.getAmount())
                .status("SUCCESS")
                .build();

        transactionService.saveTransaction(transactionDto);

        return bankResponse(userToCredit,AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE,AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE);
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
         //we can compare above by availableBalance.intValue() and debitAmount.intValue()
        /*
        we can't compare bigdecimals using < or >
        CompareTo returns
        1: when the first BigDecimal is greater than the second BigDecimal.
        0: when the first BigDecimal is equal to the second BigDecimal.
        -1: when the first BigDecimal is less than the second BigDecimal.
        */
        if(userToDebit.getAccountBalance().compareTo(request.getAmount()) == 1){
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userToDebit.setModifiedAt(LocalDateTime.now());
            userRepository.save(userToDebit);
            //Send emailAlerts After amount got credited
            //Used Text Block in setting message body. It is a feature of Java 15
            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(userToDebit.getEmail())
                    .subject("Amount Debited from your Account")
                    .messageBody(String.format("""
                                 Dear Customer,
                                 
                                 Your A/C %s is debited with INR %s on %s. Your new balance is INR %s.
                                 
                                 Account Number: %s
                                 Account Name: %s
                                 Current Balance: %s
                                 
                                 Always at your service,
                                 Team Sarvada Bank
                                 """,
                            userToDebit.getAccountNumber(),
                            request.getAmount(),
                            userToDebit.getModifiedAt(),userToDebit.getAccountBalance(),userToDebit.getAccountNumber(),
                            userToDebit.getFirstName()+ " "+userToDebit.getLastName() + " " + userToDebit.getOtherName(),userToDebit.getAccountBalance()))
                    .build();
            emailService.sendEmailAlert(emailDetails);

            TransactionDto transactionDto = TransactionDto.builder()
                    .transactionType("DEBIT")
                    .accountNumber(userToDebit.getAccountNumber())
                    .amount(request.getAmount())
                    .status("SUCCESS")
                    .build();

            transactionService.saveTransaction(transactionDto);

            return bankResponse(userToDebit,AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE,AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE);
        }else {

            TransactionDto transactionDto = TransactionDto.builder()
                    .transactionType("DEBIT")
                    .accountNumber(userToDebit.getAccountNumber())
                    .amount(request.getAmount())
                    .status("FAILURE")
                    .build();

            transactionService.saveTransaction(transactionDto);

            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
    }

    @Override
    public BankResponse transfer(TransferRequest transferRequest) {
        //get the account to debit and check the account exists or not
        //check if the amount is not more than the current balance
        //debit the amount
        //get the amount to credit
        //credit the amount
        boolean isSourceAccountExists = userRepository.existsByAccountNumber(transferRequest.getSourceAccountNumber());
        boolean isDestinationAccountExists = userRepository.existsByAccountNumber(transferRequest.getDestinationAccountNumber());
        if(!isSourceAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.DEBIT_ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.DEBIT_ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        if(!isDestinationAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.CREDIT_ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.CREDIT_ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User sourceAccountUser = userRepository.findByAccountNumber(transferRequest.getSourceAccountNumber());

        if(sourceAccountUser.getAccountBalance().compareTo(transferRequest.getAmount())>0){

            CreditDebitRequest debitRequest = CreditDebitRequest.builder()
                    .amount(transferRequest.getAmount())
                    .accountNumber(transferRequest.getSourceAccountNumber())
                    .build();
            BankResponse bankResponse = debitAccount(debitRequest);

            CreditDebitRequest creditRequest = CreditDebitRequest.builder()
                    .amount(transferRequest.getAmount())
                    .accountNumber(transferRequest.getDestinationAccountNumber())
                    .build();
            creditAccount(creditRequest);

            TransactionDto debitTransactionDto = TransactionDto.builder()
                    .transactionType("DEBIT")
                    .accountNumber(debitRequest.getAccountNumber())
                    .amount(transferRequest.getAmount())
                    .status("SUCCESS")
                    .build();

            transactionService.saveTransaction(debitTransactionDto);

            TransactionDto creditTransactionDto = TransactionDto.builder()
                    .transactionType("CREDIT")
                    .accountNumber(creditRequest.getAccountNumber())
                    .amount(transferRequest.getAmount())
                    .status("SUCCESS")
                    .build();

            transactionService.saveTransaction(creditTransactionDto);

            return bankResponse;
        }else {
            TransactionDto debitTransactionDto = TransactionDto.builder()
                    .transactionType("DEBIT")
                    .accountNumber(transferRequest.getSourceAccountNumber())
                    .amount(transferRequest.getAmount())
                    .status("FAILURE")
                    .build();

            transactionService.saveTransaction(debitTransactionDto);

            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
    }

    private BankResponse bankResponse(User user,String responseCode,String responseMessage){

        AccountInfo accountInfo = AccountInfo.builder()
                .accountName(user.getFirstName()+ " " +user.getLastName()+ " " + user.getOtherName())
                .accountNumber(user.getAccountNumber())
                .accountBalance(user.getAccountBalance())
                .build();

        return BankResponse.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .accountInfo(accountInfo)
                .build();
    }
}
