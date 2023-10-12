package com.example.telegrambotoprosnik.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Context {
    @Id
    private Long chatId;
    @Column(name = "полное_ФИО" )
    private String fullName;
    @Column(name = "тема_опроса" )
    private String questionTheme;
    @Column(name = "сложность_опроса" )
    private String questionLevel;
    @Column(name = "количество_правильных_ответов" )
    private String numberOfCorrectAnswer;
    @Column(name = "вопрос_№1" )
    private String question1;
    @Column(name = "вопрос_№2" )
    private String question2;
    @Column(name = "вопрос_№3" )
    private String question3;
    @Column(name = "вопрос_№4" )
    private String question4;
    @Column(name = "вопрос_№5" )
    private String question5;
    @Column(name = "вопрос_№6" )
    private String question6;
    @Column(name = "вопрос_№7" )
    private String question7;
    @Column(name = "вопрос_№8" )
    private String question8;
    @Column(name = "вопрос_№9" )
    private String question9;
    @Column(name = "вопрос_№10" )
    private String question10;
    @Column(name = "ответ_№1" )
    private String answer1;
    @Column(name = "ответ_№2" )
    private String answer2;
    @Column(name = "ответ_№3" )
    private String answer3;
    @Column(name = "ответ_№4" )
    private String answer4;
    @Column(name = "ответ_№5" )
    private String answer5;
    @Column(name = "ответ_№6" )
    private String answer6;
    @Column(name = "ответ_№7" )
    private String answer7;
    @Column(name = "ответ_№8" )
    private String answer8;
    @Column(name = "ответ_№9" )
    private String answer9;
    @Column(name = "ответ_№10" )
    private String answer10;
    @Column(name = "правильный_ответ_№1" )
    private String correctAnswer1;
    @Column(name = "правильный_ответ_№2" )
    private String correctAnswer2;
    @Column(name = "правильный_ответ_№3" )
    private String correctAnswer3;
    @Column(name = "правильный_ответ_№4" )
    private String correctAnswer4;
    @Column(name = "правильный_ответ_№5" )
    private String correctAnswer5;
    @Column(name = "правильный_ответ_№6" )
    private String correctAnswer6;
    @Column(name = "правильный_ответ_№7" )
    private String correctAnswer7;
    @Column(name = "правильный_ответ_№8" )
    private String correctAnswer8;
    @Column(name = "правильный_ответ_№9" )
    private String correctAnswer9;
    @Column(name = "правильный_ответ_№10" )
    private String correctAnswer10;
    private int question1Id;
    private int question2Id;
    private int question3Id;
    private int question4Id;
    private int question5Id;
    private int question6Id;
    private int question7Id;
    private int question8Id;
    private int question9Id;
    private int question10Id;
    private int questionNumber;
    @Column(name = "Последнее_действие_пользователя" )
    private LocalDateTime timeLastUsing;

}
