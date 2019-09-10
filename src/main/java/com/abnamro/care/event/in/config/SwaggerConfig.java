package com.abnamro.care.event.in.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket postsApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.abnamro.care.event.in.controller")).build()
				.apiInfo(generateApiInfo());
	}

	private ApiInfo generateApiInfo() {
		return new ApiInfoBuilder().title("Payment Arrear Event Publish API")
				.description("This API is used to create new Collection and Risk Enabling Events during "
						+ "credit Arrear life cycle. Either agreementId or customerId is mandatory in payload. "
						+ "For Ex: Customer credit agreement entering into Arrear")
				.contact(new Contact("CARE 2.0",
						"https://confluence.aws.abnamro.org/display/HTCHC/CARE+2.0+Application+Landscape",
						"care20@nl.abnamro.com"))
				.license("Care 2.0").version("Version 1.0").build();
	}

}
