package com.sravan.bank.controller;

import com.itextpdf.text.DocumentException;
import com.sravan.bank.dto.BankResponse;
import com.sravan.bank.repository.UserRepository;
import com.sravan.bank.service.BankStatementService;
import com.sravan.bank.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class TransactionController {

    private BankStatementService bankStatementService;
    private UserRepository userRepository;
    @GetMapping("/bank-statement")
    public ResponseEntity<Object> bankStatement(@RequestParam String accountNumber,
                                                           @RequestParam LocalDate fromDate,
                                                           @RequestParam LocalDate toDate) throws DocumentException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + accountNumber+" "+currentDateTime + ".pdf";

        boolean isAccountChecks = userRepository.existsByAccountNumber(accountNumber);

        if(!isAccountChecks){
            BankResponse bankResponse = isAccountExists();

            HttpHeaders httpHeaders = new HttpHeaders();
            return ResponseEntity.badRequest()
                    .headers(httpHeaders)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(bankResponse);
        }

        // Prepare the response
        ByteArrayOutputStream outputStream = bankStatementService.generateStatement(accountNumber,fromDate,toDate);
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
