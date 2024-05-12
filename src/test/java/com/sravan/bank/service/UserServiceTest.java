package com.sravan.bank.service;

import com.sravan.bank.dto.BankResponse;
import com.sravan.bank.dto.UserRequest;
import com.sravan.bank.entity.User;
import com.sravan.bank.repository.UserRepository;
import com.sravan.bank.utils.AccountUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    EmailService emailService;

    @Mock
    TransactionService transactionService;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    public void userShouldCreateAccount(){
        Mockito.when(userRepository.existsByEmail(any())).thenReturn(false);
        UserRequest createUser = UserRequest.builder()
                .firstName("Sravan")
                .lastName("Datha")
                .otherName(null)
                .gender("Male")
                .address("Rajahmundry")
                .stateOfOrigin("Andhra Pradesh")
                .email("sravandatha@gmail.com")
                .password(passwordEncoder.encode("123456789"))
                .phoneNumber("8499866865")
                .alternativePhoneNumber(null)
                .build();

        BankResponse bankResponse = userService.createAccount(createUser);

        //Uses assertj
        assertThat(bankResponse.getResponseCode()).isEqualTo("002");

    }
}
