package com.example.telegrambotoprosnik.botInterface;

import com.example.telegrambotoprosnik.model.DataBaseQuestionnaire;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KeyBoard {
    private final Logger logger = LoggerFactory.getLogger(KeyBoard.class);
    private final TelegramBot telegramBot;

    public KeyBoard(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    /**
     * Метод, принимающий клавиатуру и текст, и отправляющий ответ
     */
    public void sendResponseMenu(long chatId, ReplyKeyboardMarkup replyKeyboardMarkup, String text) {
        SendMessage sendMessage = new SendMessage(
                chatId, text).replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.description());
        }
    }

    /**
     * Метод, отображающий первое меню.
     */
    public void chooseMenu(long chatId) {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                new String[]{ButtonCommand.FIRST_THEME.getCommand(), ButtonCommand.SECOND_THEME.getCommand()},
                new String[]{ButtonCommand.THIRD_THEME.getCommand(), ButtonCommand.FOURTH_THEME.getCommand()});
        sendResponseMenu(chatId, replyKeyboardMarkup, "Выберите тему тестирования ниже");
    }

    /**
     * Метод, отображающий выбор сложности теста.
     */
    public void levelTestMenu(long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                new String[]{ButtonCommand.EASY.getCommand()},
                new String[]{ButtonCommand.MEDIUM.getCommand()},
                new String[]{ButtonCommand.HIGH.getCommand()},
                new String[]{ButtonCommand.MAIN_MENU.getCommand()});

        sendResponseMenu(chatId, replyKeyboardMarkup, "Выберите уровень сложности теста.");
    }

    /**
     * Метод, отображающий выбор сложности теста.
     */
    public void levelTestMenuEnglish(long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                new String[]{ButtonCommand.Elementary.getCommand()},
                new String[]{ButtonCommand.Beginner.getCommand()},
                new String[]{ButtonCommand.Pre_Intermediate.getCommand()},
                new String[]{ButtonCommand.Intermediate.getCommand()},
                new String[]{ButtonCommand.Upper_Intermediate_and_Advanced.getCommand()},
                new String[]{ButtonCommand.MAIN_MENU.getCommand()});

        sendResponseMenu(chatId, replyKeyboardMarkup, "Выберите уровень сложности теста.");
    }

    /**
     * метод создающий и отображающий пользователю вопросы теста.
     */
    public void answerOfTest(long chatId, DataBaseQuestionnaire question) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                new String[]{question.getAnswer1()},
                new String[]{question.getAnswer2()},
                new String[]{question.getAnswer3()},
                new String[]{question.getAnswer4()});

        sendResponseMenu(chatId, replyKeyboardMarkup, "Выберите варианты ответа ниже");
    }

    /**
     * метод создающий и отображающий пользователю вопросы теста по теме English, уровни сложности PreIntermediate и Intermediate.
     */
    public void answerOfTestPreIntermediateAndIntermediate(long chatId, DataBaseQuestionnaire question) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                new String[]{question.getAnswer1()},
                new String[]{question.getAnswer2()},
                new String[]{question.getAnswer3()},
                new String[]{question.getAnswer4()},
                new String[]{question.getAnswer5()});

        sendResponseMenu(chatId, replyKeyboardMarkup, "Выберите варианты ответа ниже");
    }

    /**
     * Метод завершения теста и возврата в главное меню
     */
    public void finishTest(long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                ButtonCommand.MAIN_MENU.getCommand());
        sendResponseMenu(chatId, replyKeyboardMarkup, "Вы хотите вернуться в меню?");
    }
}

