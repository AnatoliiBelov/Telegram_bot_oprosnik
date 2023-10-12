package com.example.telegrambotoprosnik;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class TelegramBotOprosnikApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotOprosnikApplication.class, args);


    }


}
