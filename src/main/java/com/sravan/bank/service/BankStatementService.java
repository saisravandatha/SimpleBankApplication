package com.sravan.bank.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sravan.bank.entity.Transaction;
import com.sravan.bank.entity.User;
import com.sravan.bank.repository.TransactionRepository;
import com.sravan.bank.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BankStatementService {

    private TransactionRepository transactionRepository;
    private UserRepository userRepository;

    public ByteArrayOutputStream generateStatement(String accountNumber, LocalDate startDate, LocalDate endDate) throws DocumentException {

        User user =  userRepository.findByAccountNumber(accountNumber);
        String customerName = user.getFirstName()+" "+user.getLastName()+" "+user.getOtherName();
        String customerAddress = user.getAddress();

        List<Transaction> transactionList = transactionRepository.findAll().stream()
                .filter( transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> transaction.getCreatedAt().isEqual(startDate) || transaction.getCreatedAt().isAfter(startDate) || transaction.getCreatedAt().isBefore(endDate) || transaction.getCreatedAt().isEqual(endDate))
                .toList();

        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("Setting size of document");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document,outputStream);
        document.open();

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("SARAVADHA BANK",fontTitle));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.CYAN);
        bankName.setPadding(20f);
        bankName.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPCell bankAddress = new PdfPCell(new Phrase("72, Karapakkam, Chennai-600002"));
        bankAddress.setBorder(0);
        bankAddress.setHorizontalAlignment(Element.ALIGN_CENTER);
        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        PdfPTable statementInfo = new PdfPTable(2);
        PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date: " +startDate));
        customerInfo.setBorder(0);
        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        statement.setBorder(0);
        PdfPCell stopDate = new PdfPCell(new Phrase("End Date: " +endDate));
        stopDate.setBorder(0);
        PdfPCell customerNameInfo = new PdfPCell(new Phrase("Customer Name: "+customerName));
        customerNameInfo.setBorder(0);
        PdfPCell space = new PdfPCell();
        space.setBorder(0);
        PdfPCell address = new PdfPCell(new Phrase("Customer Address: "+customerAddress));
        address.setBorder(0);
        PdfPCell space1 = new PdfPCell();
        space1.setBorder(0);
        PdfPCell space2 = new PdfPCell();
        space2.setBorder(0);
        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(stopDate);
        statementInfo.addCell(customerNameInfo);
        statementInfo.addCell(space);
        statementInfo.addCell(address);
        statementInfo.addCell(space1);
        statementInfo.addCell(space2);

        PdfPTable transactionTable = new PdfPTable(5);
        transactionTable.setWidthPercentage(100);
        PdfPCell transactionDate = new PdfPCell(new Phrase("DATE"));
        transactionDate.setBackgroundColor(BaseColor.LIGHT_GRAY);
        transactionDate.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        PdfPCell transactionID = new PdfPCell(new Phrase("TRANSACTION ID"));
        transactionID.setBackgroundColor(BaseColor.LIGHT_GRAY);
        transactionID.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor(BaseColor.LIGHT_GRAY);
        transactionType.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
        transactionAmount.setBackgroundColor(BaseColor.LIGHT_GRAY);
        transactionType.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        PdfPCell transactionStatus = new PdfPCell(new Phrase("TRANSACTION STATUS"));
        transactionStatus.setBackgroundColor(BaseColor.LIGHT_GRAY);
        transactionType.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        transactionTable.addCell(transactionDate);
        transactionTable.addCell(transactionID);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(transactionAmount);
        transactionTable.addCell(transactionStatus);

        transactionList.forEach(transaction -> {
            transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionTable.addCell(new Phrase(transaction.getTransactionId()));
            transactionTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionTable.addCell(new Phrase(transaction.getStatus()));
        });
        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionTable);
        document.close();
        return outputStream;
    }

}
