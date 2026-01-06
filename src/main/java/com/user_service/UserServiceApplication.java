package com.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<<<<<<< HEAD

@SpringBootApplication
public class UserServiceApplication {
=======
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EntityScan(basePackages = "com.user_service.entities")
@EnableJpaRepositories(basePackages = "com.user_service.repositary")
@EnableTransactionManagement
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.user_service","com.common"})
public class  UserServiceApplication {
>>>>>>> 461be25bf30961215b2a0ec748bf111b14d46c50

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
