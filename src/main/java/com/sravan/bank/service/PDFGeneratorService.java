package com.sravan.bank.service;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public interface PDFGeneratorService {

    ByteArrayOutputStream export(String accountNumber) throws IOException, DocumentException, URISyntaxException;

}
