package com.domin;

import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@SpringBootApplication
@ComponentScan(basePackages = "com.domin")
public class BackendApplication {

	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean(name = "countriesData")
	public JSONArray bootStrapCountriesData() throws RestClientException, URISyntaxException {

		try {
			RestTemplate restClient = new RestTemplate(
					new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
			return new JSONArray(
					restClient.getForEntity(new URI(env.getProperty("rest.countries.url")), String.class).getBody());
		} catch (Exception e) {
			System.err.println(e);
			throw e;
		}
	}

	@Bean
	public OpenAPI customOpenAPI(@Value("${application-description}") String appDesciption,
			@Value("${application-version}") String appVersion) {
		return new OpenAPI()// .servers(servers)
				.info(new Info().title("Domin Application API").version(appVersion).description(appDesciption)
						.termsOfService("http://swagger.io/terms/")
						.license(new License().name("Apache 2.0").url("http://springdoc.org")));

	}

}
