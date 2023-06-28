package com.sravan.bank.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sravan.bank.dto.BankResponse;
import com.sravan.bank.entity.Transaction;
import com.sravan.bank.repository.TransactionRepository;
import com.sravan.bank.repository.UserRepository;
import com.sravan.bank.utils.AccountUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@Service
public class PDFGeneratorServiceImpl implements PDFGeneratorService, Serializable {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TransactionRepository transactionRepository;
    @Override
    public ByteArrayOutputStream export(String accountNumber) throws IOException, DocumentException, URISyntaxException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document  document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        Paragraph paragraph = new Paragraph("Transaction List",fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);

        List<Transaction> transactionDtoList = transactionRepository.findByAccountNumber(accountNumber.toString());

        if(transactionDtoList.size()==0){
           isTransactionsExists();
        }
        float[] columnWidths = {2, 5, 5};

        PdfPTable table = new PdfPTable(3);
        table.setSpacingBefore(25);
        table.setWidths(columnWidths);
        table.setWidthPercentage(100);
        table.setSpacingAfter(25);
        addCustomRows(table);

        // Add transaction details to the document
        for (Transaction transaction : transactionDtoList) {
            PdfPCell c1 = new PdfPCell(new Phrase(transaction.getTransactionId()));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(transaction.getTransactionType()));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(transaction.getAmount().toString()));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);
        }

        document.add(table);
        document.close();
        return outputStream;
    }

    private void addCustomRows(PdfPTable table) throws URISyntaxException, BadElementException, IOException {
        PdfPCell c1 = new PdfPCell(new Phrase("Transaction ID "));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Transaction Type"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Amount"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
    }


    public BankResponse isTransactionsExists(){
        return BankResponse.builder()
                .responseCode(AccountUtils.NO_TRANSACTIONS_FOUND_CODE)
                .responseMessage(AccountUtils.NO_TRANSACTIONS_FOUND_MESSAGE)
                .accountInfo(null)
                .build();
    }

}
