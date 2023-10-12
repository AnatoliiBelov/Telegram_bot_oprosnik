package com.example.telegrambotoprosnik.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "data_base_questionnaire")
public class DataBaseQuestionnaire  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name="theme")
    private String theme;
    @Column(name="level")
    private String level;
    @Column(name="question")
    private String question;
    @Column(name = "answer1")
    private String answer1;
    @Column(name = "answer2")
    private String answer2;
    @Column(name = "answer3")
    private String answer3;
    @Column(name = "answer4")
    private String answer4;
    @Column(name = "answer5")
    private String answer5;
    @Column(name = "correct_answer")
    private String correctAnswer;


}
