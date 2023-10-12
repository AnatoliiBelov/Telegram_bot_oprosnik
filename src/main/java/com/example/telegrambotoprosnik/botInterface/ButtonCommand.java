package com.example.telegrambotoprosnik.botInterface;

public enum ButtonCommand {
    START("/start"),
    FIRST_THEME("Проверка уровня знаний по английскому языку"),
    SECOND_THEME("Тема 2"),
    THIRD_THEME("Тема 3"),
    FOURTH_THEME("Тема 4"),
    EASY("Лёгкий"),
    MEDIUM("Средний"),
    HIGH("Сложный"),
    MAIN_MENU2("/menu"),
    DEFAULT("По умолчанию"),
    Elementary("Elementary"),
    Beginner("Beginner"),
    Pre_Intermediate("Pre-Intermediate"),
    Intermediate("Intermediate"),
    Upper_Intermediate_and_Advanced("Upper Intermediate and Advanced"),
    MAIN_MENU("Главное меню");



    ButtonCommand(String command) {
        this.command = command;
    }

    private final String command;


    public String getCommand() {
        return command;
    }
}
