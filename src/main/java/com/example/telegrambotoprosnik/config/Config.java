package com.example.telegrambotoprosnik.config;
import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class Config {

    @Bean
    public TelegramBot telegramBot(@Value("${bot.token}") String token){

        return new TelegramBot(token);

    }
}
