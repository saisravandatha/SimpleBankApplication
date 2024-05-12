package com.sravan.bank.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.sravan.bank.dto.*;
import com.sravan.bank.entity.Transaction;
import com.sravan.bank.service.BankStatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.DocumentException;
import com.sravan.bank.repository.UserRepository;
import com.sravan.bank.service.PDFGeneratorService;
import com.sravan.bank.service.UserService;
import com.sravan.bank.utils.AccountUtils;

import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "User Account Management API's")
@RequestMapping("/api/user")
public class BankController {

    @Autowired
    UserService userService;

    @Autowired
    PDFGeneratorService pdfGeneratorService;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public String showHome(Model model){
        return "index";
    }
    @GetMapping("/create")
    public ModelAndView showCreateUserForm(Model model) {
        ModelAndView mav = new ModelAndView("Transactions");
        mav.addObject("formData",new UserRequest());
        return mav;
    }

//    @PostMapping("/create-account")
//    public String createAccount1(@ModelAttribute UserRequest userRequest,Model model) {
//        BankResponse bankResponse = userService.createAccount(userRequest);
//        model.addAttribute("formData",userRequest);
//        return "display_form";
//    }


    @PostMapping("/login")
    public BankResponse login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }


    @PostMapping("/create-account")
    @Operation(
            summary = "Create New User Account",
            description = "Creating a new user account and assigning a account ID"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 Created"
    )
    public ResponseEntity<BankResponse> createAccount(@RequestBody UserRequest userRequest) {
        BankResponse bankResponse = userService.createAccount(userRequest);
        return new ResponseEntity<>(bankResponse, HttpStatus.CREATED);
    }

    @GetMapping("/balance-enquiry")
    public String balanceEnquiry(@ModelAttribute EnquiryRequest enquiryRequest,Model model) {
        BankResponse bankResponse = userService.balanceEnquiry(enquiryRequest);
        model.addAttribute("formData",enquiryRequest);
        return "balance-enquiry";
    }
// Not in use
//    @GetMapping("/balanceEnquiry")
//    public ResponseEntity<BankResponse> balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
//        BankResponse bankResponse = userService.balanceEnquiry(enquiryRequest);
//        return new ResponseEntity<>(bankResponse, HttpStatus.FOUND);
//    }

    @GetMapping("/name-enquiry")
    public ResponseEntity<String> nameEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
        return new ResponseEntity<>(String.format("Account Holder name: %s", userService.nameEnquiry(enquiryRequest)), HttpStatus.FOUND);
    }

    @PostMapping("/credit")
    public ResponseEntity<BankResponse> creditAmount(@RequestBody CreditDebitRequest creditDebitRequest) {
        return new ResponseEntity<>(userService.creditAccount(creditDebitRequest), HttpStatus.OK);
    }

    @PostMapping("/debit")
    public ResponseEntity<BankResponse> debitAccount(@RequestBody CreditDebitRequest creditDebitRequest) {
        return new ResponseEntity<>(userService.debitAccount(creditDebitRequest), HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<BankResponse> transferAmountBetweenAccounts(@RequestBody TransferRequest transferRequest) {
        return new ResponseEntity<>(userService.transfer(transferRequest), HttpStatus.OK);
    }

    @GetMapping("/pdf/{accountNumber}")
    public ResponseEntity<Object> generateTransactionPDF(@PathVariable String accountNumber) throws DocumentException, IOException, URISyntaxException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pdf_" + currentDateTime + ".pdf";

        ByteArrayOutputStream outputStream = pdfGeneratorService.export(accountNumber);

        boolean isAccountChecks = userRepository.existsByAccountNumber(accountNumber.toString());

        if(!isAccountChecks){
            BankResponse bankResponse = isAccountExists();

            HttpHeaders httpHeaders = new HttpHeaders();
            return ResponseEntity.badRequest()
                    .headers(httpHeaders)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(bankResponse);
        }

        // Prepare the response
        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add(headerKey,headerValue);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }


    public BankResponse isAccountExists(){
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                .accountInfo(null)
                .build();
    }



}
