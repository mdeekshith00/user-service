package com.user_service.configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class UserConfig {

    @Bean
    WebClient donorWebClient(WebClient.Builder builder) {
        return builder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }

    // Java 17 compatible executor
    @Bean
    Executor taskExecutor() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Bean
    WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> factory.addConnectorCustomizers(
                connector -> connector.getProtocolHandler()
                        .setExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()))
        );
    }

	/*
	 * @Bean public OpenAPI openAPI() { return new OpenAPI() .addSecurityItem(new
	 * SecurityRequirement().addList("Bearer Authentication")) .components(new
	 * Components().addSecuritySchemes("Bearer Authentication", new SecurityScheme()
	 * .name("Authorization") .type(SecurityScheme.Type.HTTP) .scheme("bearer")
	 * .bearerFormat("JWT") )) .info(new Info() .title("User-Service API")
	 * .version("1.0")); }
	 */
}
