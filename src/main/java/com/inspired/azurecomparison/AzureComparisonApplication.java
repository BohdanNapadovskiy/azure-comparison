package com.inspired.azurecomparison;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

@SpringBootApplication
public class AzureComparisonApplication {

	public static void main(String[] args) {
		SpringApplication.run(AzureComparisonApplication.class, args);
	}

}
