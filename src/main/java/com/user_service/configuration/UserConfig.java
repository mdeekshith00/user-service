package com.user_service.configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.modelmapper.ModelMapper;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class UserConfig {

    @Bean
    WebClient donorWebClient(WebClient.Builder builder) {
        return builder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }
	@Bean
     Executor virtualThreadExecutor() {
		return Executors.newVirtualThreadPerTaskExecutor();
	}
	@Bean
	 WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
	    return factory -> factory.addConnectorCustomizers(connector ->
	        connector.getProtocolHandler().setExecutor(Executors.newVirtualThreadPerTaskExecutor())
	    );
	}

	@Bean
	 OpenAPI openAPI() {
	    return new OpenAPI()
	        .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
	        .components(new Components().addSecuritySchemes("Bearer Authentication",
	            new SecurityScheme()
	                .name("Authorization")
	                .type(SecurityScheme.Type.HTTP)
	                .scheme("bearer")
	                .bearerFormat("JWT")
	        ))
	        .info(new Info().title("User-Service API").version("1.0"));
	}
	
	

}
