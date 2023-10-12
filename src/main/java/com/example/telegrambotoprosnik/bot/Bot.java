package com.example.telegrambotoprosnik.bot;
import com.example.telegrambotoprosnik.botInterface.ButtonCommand;
import com.example.telegrambotoprosnik.botInterface.KeyBoard;
import com.example.telegrambotoprosnik.model.Context;
import com.example.telegrambotoprosnik.model.DataBaseQuestionnaire;
import com.example.telegrambotoprosnik.service.ContextService;
import com.example.telegrambotoprosnik.service.DataBaseQuestionnaireService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.example.telegrambotoprosnik.botInterface.ButtonCommand.*;

@Component
public class Bot implements UpdatesListener {
    private final Logger logger = LoggerFactory.getLogger(Bot.class);
    private final KeyBoard keyBoard;
    private final TelegramBot telegramBot;
    private final ContextService contextService;
    private final DataBaseQuestionnaireService dataBaseQuestionnaireService;
    private final Pattern pattern = Pattern.compile("^([А-Яа-яёЁ]{2,20})( [А-Яа-яёЁ]{2,20})( [А-Яа-яёЁ]{2,20})?( [А-Яа-яёЁ]{2,20})?$");
    @Value("${administrator.chatId}")
    private Long administratorChatId;

    private List<String> answerCatalog;

    public Bot(KeyBoard keyBoard, TelegramBot telegramBot, ContextService contextService,
               DataBaseQuestionnaireService dataBaseQuestionnaireService) {
        this.keyBoard = keyBoard;
        this.telegramBot = telegramBot;
        this.contextService = contextService;
        this.dataBaseQuestionnaireService = dataBaseQuestionnaireService;

    }


    @PostConstruct
    private void init() {
        telegramBot.setUpdatesListener(this);
        List<String> answers = new ArrayList<>();
        for (com.example.telegrambotoprosnik.model.DataBaseQuestionnaire dataBaseQuestionnaire :
                dataBaseQuestionnaireService.getAll()) {
            answers.add(dataBaseQuestionnaire.getAnswer1());
            answers.add(dataBaseQuestionnaire.getAnswer2());
            answers.add(dataBaseQuestionnaire.getAnswer3());
            answers.add(dataBaseQuestionnaire.getAnswer4());
            if (dataBaseQuestionnaire.getAnswer5() != null) {
                answers.add(dataBaseQuestionnaire.getAnswer5());
            }
        }
        answerCatalog = answers;

    }


    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(update -> {
                        logger.info("Handles update: {}", update);
                        Long chatId = checkChatId(update);
                        String messageText = checkMessage(update);
                        if (pattern.matcher(messageText).matches()&&!messageText.equals("Главное меню")) {

                            Context context = contextService.getByChatId(chatId).get();
                            String theme = context.getQuestionTheme();
                            String level = context.getQuestionLevel();
                            startFirstQuestion(chatId, context, theme, level, messageText);
                        } else if
                        (answerCatalog.contains(messageText)) {
                            Context context = contextService.getByChatId(chatId).get();
                            try {
                                startQuestionForUser(context, chatId, messageText);
                            } catch (SQLException | IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            switch (((parse(messageText)))) {
                                case START -> {
                                    if (contextService.getByChatId(chatId).isPresent()){
                                        contextService.deleteByChatId(chatId);}
                                    sendResponseMessage(chatId, "Здравствуйте, " + update.message().chat().firstName() + "! " +
                                            "я телеграм-бот для проверки знаний. Выберите, пожалуйста, " +
                                            "интересующее Вас направление, сложность теста, введите своё ФИО и начнём тестирование");
                                    keyBoard.chooseMenu(chatId);

                                }
                                case MAIN_MENU2, MAIN_MENU ->
                                {if (contextService.getByChatId(chatId).isPresent()){
                                    contextService.deleteByChatId(chatId);}
                                    keyBoard.chooseMenu(chatId);}
                                case FIRST_THEME -> saveContextOnSecondMenuEnglish(FIRST_THEME, chatId);
                                case SECOND_THEME -> saveContextOnSecondMenu(ButtonCommand.SECOND_THEME, chatId);
                                case THIRD_THEME -> saveContextOnSecondMenu(ButtonCommand.THIRD_THEME, chatId);
                                case FOURTH_THEME -> saveContextOnSecondMenu(ButtonCommand.FOURTH_THEME, chatId);
                                case Elementary -> saveLevelAndStartFirstQuestion(chatId, Elementary, messageText, FIRST_THEME);
                                case Beginner -> saveLevelAndStartFirstQuestion(chatId, Beginner, messageText, FIRST_THEME);
                                case Pre_Intermediate ->
                                        saveLevelAndStartFirstQuestion(chatId, Pre_Intermediate, messageText, FIRST_THEME);
                                case Intermediate ->
                                        saveLevelAndStartFirstQuestion(chatId, Intermediate, messageText, FIRST_THEME);
                                case Upper_Intermediate_and_Advanced ->
                                        saveLevelAndStartFirstQuestion(chatId, Upper_Intermediate_and_Advanced, messageText, FIRST_THEME);
                                case EASY -> {
                                    Context context = contextService.getByChatId(chatId).get();
                                    context.setQuestionLevel(EASY.getCommand());
                                    context.setTimeLastUsing(LocalDateTime.now());
                                    contextService.saveContext(context);
                                    switch (context.getQuestionTheme()) {
                                        case "Тема 2" ->
                                                startFirstQuestion(chatId, context, SECOND_THEME.getCommand(), EASY.getCommand(), messageText);

                                        case "Тема 3" ->
                                                startFirstQuestion(chatId, context, THIRD_THEME.getCommand(), EASY.getCommand(), messageText);

                                        case "Тема 4" ->
                                                startFirstQuestion(chatId, context, FOURTH_THEME.getCommand(), EASY.getCommand(), messageText);

                                    }
                                }
                                case MEDIUM -> {
                                    Context context = contextService.getByChatId(chatId).get();
                                    context.setQuestionLevel(MEDIUM.getCommand());
                                    context.setTimeLastUsing(LocalDateTime.now());
                                    contextService.saveContext(context);
                                    switch (context.getQuestionTheme()) {
                                        case "Тема 2" ->
                                                startFirstQuestion(chatId, context, SECOND_THEME.getCommand(), MEDIUM.getCommand(), messageText);

                                        case "Тема 3" ->
                                                startFirstQuestion(chatId, context, THIRD_THEME.getCommand(), MEDIUM.getCommand(), messageText);

                                        case "Тема 4" ->
                                                startFirstQuestion(chatId, context, FOURTH_THEME.getCommand(), MEDIUM.getCommand(), messageText);

                                    }
                                }
                                case HIGH -> {
                                    Context context = contextService.getByChatId(chatId).get();
                                    context.setQuestionLevel(HIGH.getCommand());
                                    context.setTimeLastUsing(LocalDateTime.now());
                                    contextService.saveContext(context);
                                    switch (context.getQuestionTheme()) {
                                        case "Тема 2" ->
                                                startFirstQuestion(chatId, context, SECOND_THEME.getCommand(), HIGH.getCommand(), messageText);

                                        case "Тема 3" ->
                                                startFirstQuestion(chatId, context, THIRD_THEME.getCommand(), HIGH.getCommand(), messageText);

                                        case "Тема 4" ->
                                                startFirstQuestion(chatId, context, FOURTH_THEME.getCommand(), HIGH.getCommand(), messageText);

                                    }

                                }
                                case DEFAULT -> sendResponseMessage(chatId, "Вы ввели некорректную команду");
                            }


                        }
                    }
            );
        } catch (
                Exception e) {
            logger.error(e.getMessage(), e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void saveLevelAndStartFirstQuestion(Long chatId, ButtonCommand level, String messageText, ButtonCommand theme) {
        Context context = contextService.getByChatId(chatId).get();
        context.setQuestionLevel(level.getCommand());
        context.setTimeLastUsing(LocalDateTime.now());
        contextService.saveContext(context);
        startFirstQuestion(chatId, context, theme.getCommand(), level.getCommand(), messageText);

    }

    private void startFirstQuestion(Long chatId, Context context, String theme, String level, String message) {
        if (context.getQuestionNumber() == 0 && context.getFullName() == null) {
            context.setQuestionNumber(-1);
            sendResponseMessage(chatId, "Введите свою фамилию, имя и отчество на русском языке. Пример: \n" +
                    "Иванов Иван Иванович");
            context.setTimeLastUsing(LocalDateTime.now());
            contextService.saveContext(context);


        } else {
            if (context.getQuestionNumber() == -1) {
                context.setFullName(message);
            }

            DataBaseQuestionnaire question =
                    checkRepeatingQuestion(context, theme, level);
            context.setQuestionNumber(1);
            context.setQuestion1Id(question.getId());
            context.setQuestion1(question.getQuestion());
            context.setCorrectAnswer1(question.getCorrectAnswer());
            context.setQuestion1Id(question.getId());
            context.setTimeLastUsing(LocalDateTime.now());
            contextService.saveContext(context);
            sendResponseMessage(chatId, "Вопрос: "+  question.getQuestion());
            chooseKeyboard(context, chatId, question);

        }
    }

    private void chooseKeyboard(Context context, Long chatId, DataBaseQuestionnaire question) {
        if (context.getQuestionLevel().equals(Pre_Intermediate.getCommand()) ||
                context.getQuestionLevel().equals(Intermediate.getCommand())) {
            keyBoard.answerOfTestPreIntermediateAndIntermediate(chatId, question);

        } else {
            keyBoard.answerOfTest(chatId, question);
        }

    }

    /**
     * Метод, запускающий вопросы, записывающий в контекст сами вопросы, ответы пользователя и правильные ответы. Если
     * отвеченных вопросов менее 10, то запускает сл вопрос. Если отвеченных вопрос 10,
     * то запускает метод записи результатов в БД
     */


    private void startQuestionForUser(Context context, Long chatId, String message) throws SQLException, IOException {

        String theme = context.getQuestionTheme();
        String level = context.getQuestionLevel();
        context.setQuestionNumber(context.getQuestionNumber() + 1);
        context.setTimeLastUsing(LocalDateTime.now());
        contextService.saveContext(context);
        DataBaseQuestionnaire question = checkRepeatingQuestion(context, theme, level);

        if (context.getQuestionNumber() == 2) {
            context.setAnswer1(message);
            context.setQuestion2(question.getQuestion());
            context.setCorrectAnswer2(question.getCorrectAnswer());
            context.setQuestion2Id(question.getId());
            context.setTimeLastUsing(LocalDateTime.now());
            contextService.saveContext(context);
            sendResponseMessage(chatId, "Вопрос: "+ question.getQuestion());
            chooseKeyboard(context, chatId, question);
        } else if (context.getQuestionNumber() == 3) {
            context.setAnswer2(message);
            context.setQuestion3(question.getQuestion());
            context.setCorrectAnswer3(question.getCorrectAnswer());
            context.setQuestion3Id(question.getId());
            context.setTimeLastUsing(LocalDateTime.now());
            contextService.saveContext(context);
            sendResponseMessage(chatId, "Вопрос: "+ question.getQuestion());
            chooseKeyboard(context, chatId, question);
        } else if (context.getQuestionNumber() == 4) {
            context.setAnswer3(message);
            context.setQuestion4(question.getQuestion());
            context.setQuestion4(question.getQuestion());
            context.setCorrectAnswer4(question.getCorrectAnswer());
            context.setQuestion4Id(question.getId());
            context.setTimeLastUsing(LocalDateTime.now());
            contextService.saveContext(context);
            sendResponseMessage(chatId, "Вопрос: "+ question.getQuestion());
            chooseKeyboard(context, chatId, question);
        } else if (context.getQuestionNumber() == 5) {
            context.setAnswer4(message);
            context.setQuestion5(question.getQuestion());
            context.setCorrectAnswer5(question.getCorrectAnswer());
            context.setQuestion5Id(question.getId());
            context.setTimeLastUsing(LocalDateTime.now());
            contextService.saveContext(context);
            sendResponseMessage(chatId, "Вопрос: "+ question.getQuestion());
            chooseKeyboard(context, chatId, question);
        } else if (context.getQuestionNumber() == 6) {
            context.setAnswer5(message);
            context.setQuestion6(question.getQuestion());
            context.setCorrectAnswer6(question.getCorrectAnswer());
            context.setQuestion6Id(question.getId());
            context.setTimeLastUsing(LocalDateTime.now());
            contextService.saveContext(context);
            sendResponseMessage(chatId, "Вопрос: "+ question.getQuestion());
            chooseKeyboard(context, chatId, question);
        } else if (context.getQuestionNumber() == 7) {
            context.setAnswer6(message);
            context.setQuestion7(question.getQuestion());
            context.setCorrectAnswer7(question.getCorrectAnswer());
            context.setQuestion7Id(question.getId());
            context.setTimeLastUsing(LocalDateTime.now());
            contextService.saveContext(context);
            sendResponseMessage(chatId, "Вопрос: "+ question.getQuestion());
            chooseKeyboard(context, chatId, question);
        } else if (context.getQuestionNumber() == 8) {
            context.setAnswer7(message);
            context.setQuestion8(question.getQuestion());
            context.setCorrectAnswer8(question.getCorrectAnswer());
            context.setQuestion8Id(question.getId());
            context.setTimeLastUsing(LocalDateTime.now());
            contextService.saveContext(context);
            sendResponseMessage(chatId, "Вопрос: "+ question.getQuestion());
            chooseKeyboard(context, chatId, question);
        } else if (context.getQuestionNumber() == 9) {
            context.setAnswer8(message);
            context.setQuestion9(question.getQuestion());
            context.setCorrectAnswer9(question.getCorrectAnswer());
            context.setQuestion9Id(question.getId());
            context.setTimeLastUsing(LocalDateTime.now());
            contextService.saveContext(context);
            sendResponseMessage(chatId, "Вопрос: "+ question.getQuestion());
            chooseKeyboard(context, chatId, question);
        } else if (context.getQuestionNumber() == 10) {
            context.setAnswer9(message);
            context.setQuestion10(question.getQuestion());
            context.setCorrectAnswer10(question.getCorrectAnswer());
            context.setQuestion10Id(question.getId());
            context.setTimeLastUsing(LocalDateTime.now());
            contextService.saveContext(context);
            sendResponseMessage(chatId, "Вопрос: "+ question.getQuestion());
            chooseKeyboard(context, chatId, question);
        } else if (context.getQuestionNumber() == 11) {
            context.setAnswer10(message);
            context.setTimeLastUsing(LocalDateTime.now());
            sendResponseMessage(chatId, "Поздравляю! Вы завершили тест!");
            contextService.saveContext(context);
            sendResultsToAdministrator(chatId);
            keyBoard.finishTest(chatId);
        }
    }

    private void sendResultsToAdministrator(Long chatId) throws SQLException, IOException {
        Context context = contextService.getByChatId(chatId).get();
        context.setNumberOfCorrectAnswer(""+checkCorrectAnswer(chatId)+"");
        contextService.saveContext(context);
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/tg_bot_oprosnik", "postgres", "CRaZy1992");
        CopyManager copyManager = new CopyManager((BaseConnection) connection);
        FileOutputStream fileOutputStream = new FileOutputStream("C:/temp/123.csv");

        String sqlCommand = """
                COPY (select name, value
                                                        from  context c
                                                        cross join
                                                        UNNEST(array[полное_фио,
                                                                               тема_опроса,
                                                                               сложность_опроса,
                                                                               количество_правильных_ответов,
                                                                               вопрос_№1,
                                                                               ответ_№1,
                                                                               правильный_ответ_№1,
                                                                               вопрос_№2,
                                                                               ответ_№2,
                                                                               правильный_ответ_№2,
                                                                               вопрос_№3,
                                                                               ответ_№3,
                                                                               правильный_ответ_№3,
                                                                               вопрос_№4,
                                                                               ответ_№4,
                                                                               правильный_ответ_№4,
                                                                               вопрос_№5,
                                                                               ответ_№5,
                                                                               правильный_ответ_№5,
                                                                               вопрос_№6,
                                                                               ответ_№6,
                                                                               правильный_ответ_№6,
                                                                               вопрос_№7,
                                                                               ответ_№7,
                                                                               правильный_ответ_№7,
                                                                               вопрос_№8,
                                                                               ответ_№8,
                                                                               правильный_ответ_№8,
                                                                               вопрос_№9,
                                                                               ответ_№9,
                                                                               правильный_ответ_№9,
                                                                               вопрос_№10,
                                                                               ответ_№10,
                                                                               правильный_ответ_№10],
                                                        array[ 'полное_фио',
                                                                               'тема_опроса',
                                                                               'сложность_опроса',
                                                                               'количество_правильных_ответов',
                                                                               'вопрос_№1',
                                                                               'ответ_№1',
                                                                               'правильный_ответ_№1',
                                                                               'вопрос_№2',
                                                                               'ответ_№2',
                                                                               'правильный_ответ_№2',
                                                                               'вопрос_№3',
                                                                               'ответ_№3',
                                                                               'правильный_ответ_№3',
                                                                               'вопрос_№4',
                                                                               'ответ_№4',
                                                                               'правильный_ответ_№4',
                                                                               'вопрос_№5',
                                                                               'ответ_№5',
                                                                               'правильный_ответ_№5',
                                                                               'вопрос_№6',
                                                                               'ответ_№6',
                                                                               'правильный_ответ_№6',
                                                                               'вопрос_№7',
                                                                               'ответ_№7',
                                                                               'правильный_ответ_№7',
                                                                               'вопрос_№8',
                                                                               'ответ_№8',
                                                                               'правильный_ответ_№8',
                                                                               'вопрос_№9',
                                                                               'ответ_№9',
                                                                               'правильный_ответ_№9',
                                                                               'вопрос_№10',
                                                                               'ответ_№10',
                                                                               'правильный_ответ_№10'])
                                                                               as t(value, name )
                                                                               WHERE chat_Id=""" + chatId + ") " +
                "TO STDOUT csv header";
        copyManager.copyOut(sqlCommand, fileOutputStream);
        byte[] bytes1 = Files.readAllBytes(Path.of("C:/temp/123.csv"));

        SendDocument sendResults = new SendDocument(administratorChatId, bytes1);
        sendResponseMessage(administratorChatId, "Добрый день! Пользователь " + context.getFullName() +
                " прошел опрос. Результаты вы можете посмотреть по ссылке");
        sendResults.fileName("Результаты.csv");
        SendResponse sendResponse = telegramBot.execute(sendResults);
        contextService.deleteByChatId(chatId);
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.description());
        }
    }

    private int checkCorrectAnswer(Long chatId) {
        int numberCorrectAnswer = 0;
        Context context = contextService.getByChatId(chatId).get();
        if (context.getAnswer1().equals(context.getCorrectAnswer1())) {
            numberCorrectAnswer++;
        }
        if (context.getAnswer2().equals(context.getCorrectAnswer2())) {
            numberCorrectAnswer++;
        }
        if (context.getAnswer3().equals(context.getCorrectAnswer3())) {
            numberCorrectAnswer++;
        }
        if (context.getAnswer4().equals(context.getCorrectAnswer4())) {
            numberCorrectAnswer++;
        }
        if (context.getAnswer5().equals(context.getCorrectAnswer5())) {
            numberCorrectAnswer++;
        }
        if (context.getAnswer6().equals(context.getCorrectAnswer6())) {
            numberCorrectAnswer++;
        }
        if (context.getAnswer7().equals(context.getCorrectAnswer7())) {
            numberCorrectAnswer++;
        }
        if (context.getAnswer8().equals(context.getCorrectAnswer8())) {
            numberCorrectAnswer++;
        }
        if (context.getAnswer9().equals(context.getCorrectAnswer9())) {
            numberCorrectAnswer++;
        }
        if (context.getAnswer10().equals(context.getCorrectAnswer10())) {
            numberCorrectAnswer++;
        }
        return numberCorrectAnswer;
    }


    private Long checkChatId(Update update) {
        if (update.callbackQuery() != null) {
            return update.callbackQuery().message().chat().id();
        } else {
            return update.message().chat().id();
        }

    }

    private String checkMessage(Update update) {
        if (update.callbackQuery() != null) {
            return update.callbackQuery().data();
        } else {
            return update.message().text();
        }
    }

    private static ButtonCommand parse(String buttonCommand) {
        ButtonCommand[] values = ButtonCommand.values();
        for (ButtonCommand command : values) {
            if (command.getCommand().equals(buttonCommand)) {
                return command;
            }
        }
        return DEFAULT;
    }

    private void sendResponseMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId, text);
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.description());
        }
    }

    private void saveContextOnSecondMenu(ButtonCommand buttonCommand, Long chatId) {
        Context context = new Context();
        context.setChatId(chatId);
        context.setTimeLastUsing(LocalDateTime.now());
        contextService.saveContext(context);
        if (context.getQuestionTheme() == null) {
            context.setQuestionTheme(buttonCommand.getCommand());

            contextService.saveContext(context);
        }
        sendResponseMessage(chatId, "Вы выбрали тему проверки знаний: " + buttonCommand.getCommand() +
                ". Выберите, пожалуйста, уровень теста ниже");
        keyBoard.levelTestMenu(chatId);
    }

    private void saveContextOnSecondMenuEnglish(ButtonCommand buttonCommand, Long chatId) {
        Context context = new Context();
        context.setChatId(chatId);
        context.setTimeLastUsing(LocalDateTime.now());
        contextService.saveContext(context);
        if (context.getQuestionTheme() == null) {
            context.setQuestionTheme(buttonCommand.getCommand());

            contextService.saveContext(context);
        }
        sendResponseMessage(chatId, "Вы выбрали тему проверки знаний: " + buttonCommand.getCommand() +
                ". Выберите, пожалуйста, уровень теста ниже");
        keyBoard.levelTestMenuEnglish(chatId);
    }

    private DataBaseQuestionnaire checkRepeatingQuestion(Context context, String theme, String level) {

        List<Integer> idQuestion = dataBaseQuestionnaireService.iDQuestionByThemeAndLevel(theme, level);


        if (idQuestion.contains(context.getQuestion1Id())) {
            idQuestion.remove((Integer) context.getQuestion1Id());
        }
        if (idQuestion.contains(context.getQuestion2Id())) {
            idQuestion.remove((Integer) context.getQuestion2Id());
        }
        if (idQuestion.contains(context.getQuestion3Id())) {
            idQuestion.remove((Integer) context.getQuestion3Id());
        }
        if (idQuestion.contains(context.getQuestion4Id())) {
            idQuestion.remove((Integer) context.getQuestion4Id());
        }
        if (idQuestion.contains(context.getQuestion5Id())) {
            idQuestion.remove((Integer) context.getQuestion5Id());
        }
        if (idQuestion.contains(context.getQuestion6Id())) {
            idQuestion.remove((Integer) context.getQuestion6Id());
        }
        if (idQuestion.contains(context.getQuestion7Id())) {
            idQuestion.remove((Integer) context.getQuestion7Id());
        }
        if (idQuestion.contains(context.getQuestion8Id())) {
            idQuestion.remove((Integer) context.getQuestion8Id());
        }
        if (idQuestion.contains(context.getQuestion9Id())) {
            idQuestion.remove((Integer) context.getQuestion9Id());
        }
        int generateQuestionIndex = (int) (Math.random() * (idQuestion.size() - 1));
        int newIdQuestion = idQuestion.get(generateQuestionIndex);
        return this.dataBaseQuestionnaireService.getDataBaseQuestionnaire(newIdQuestion);

    }

}