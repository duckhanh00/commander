package com.commander.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class CommanderApplication {
  public static void main(String[] args) {
    System.setProperty("jasypt.encryptor.password", "finance-core-project");
    SpringApplication.run(CommanderApplication.class, args);
  }
}
