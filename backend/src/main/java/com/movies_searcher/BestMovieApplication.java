package com.movies_searcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class BestMovieApplication {

	public static void main(String[] args) {
		SpringApplication.run(BestMovieApplication.class, args);
	}

	@Bean
	public RestClient.Builder restClientBuilder() {
		return RestClient.builder();
	}
}
