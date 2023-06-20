package com.sravan.bank.controller;

import com.sravan.bank.dto.*;
import com.sravan.bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class BankController {

    @Autowired
    UserService userService;

    @PostMapping("/createAccount")
    public ResponseEntity<BankResponse> createAccount(@RequestBody UserRequest userRequest){
        BankResponse bankResponse = userService.createAccount(userRequest);
        return new ResponseEntity<>(bankResponse,HttpStatus.CREATED);
    }

    @GetMapping("/balanceEnquiry")
    public ResponseEntity<BankResponse> balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        BankResponse bankResponse = userService.balanceEnquiry(enquiryRequest);
        return new ResponseEntity<>(bankResponse,HttpStatus.FOUND);
    }

    @GetMapping("/nameEnquiry")
    public ResponseEntity<String> nameEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        return new ResponseEntity<>(String.format("Account Holder name: %s",userService.nameEnquiry(enquiryRequest)),HttpStatus.FOUND);
    }

    @PostMapping("/credit")
    public ResponseEntity<BankResponse> creditAmount(@RequestBody CreditDebitRequest creditDebitRequest){
        return new ResponseEntity<>(userService.creditAccount(creditDebitRequest),HttpStatus.OK);
    }

    @PostMapping("/debit")
    public ResponseEntity<BankResponse> debitAccount(@RequestBody CreditDebitRequest creditDebitRequest){
        return new ResponseEntity<>(userService.debitAccount(creditDebitRequest),HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<BankResponse> transferAmountBetweenAccounts(@RequestBody TransferRequest transferRequest){
        return new ResponseEntity<>(userService.transfer(transferRequest),HttpStatus.OK);
    }
}
