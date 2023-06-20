package com.sravan.bank;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.Version;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
		title = "Sarvadha Bank Api",
		description = "Backend Rest API's for the Bank",
		contact =  @Contact(name = "Sravan Datha",
				email = "sravandatha999@gmail.com",
				url = "https://github.com/saisravandatha/SimpleBankApplication"),
		license = @License(
						name = "Sarvadha Bank")
))
public class BankApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}
}
