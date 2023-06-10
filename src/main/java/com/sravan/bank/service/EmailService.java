package com.sravan.bank.service;

import com.sravan.bank.dto.EmailDetails;

public interface EmailService {

    void sendEmailAlert(EmailDetails emailDetails);

}
