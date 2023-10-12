package com.example.telegrambotoprosnik.service;

import com.example.telegrambotoprosnik.model.DataBaseQuestionnaire;
import com.example.telegrambotoprosnik.repisitory.DataBaseQuestionnaireRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataBaseQuestionnaireService {
    DataBaseQuestionnaireRepository dataBaseQuestionnaireRepository;

    public DataBaseQuestionnaireService(DataBaseQuestionnaireRepository dataBaseQuestionnaireRepository) {
        this.dataBaseQuestionnaireRepository = dataBaseQuestionnaireRepository;
    }

    public DataBaseQuestionnaire getDataBaseQuestionnaire(int idQuestion) {
        return dataBaseQuestionnaireRepository
                .findDataBaseQuestionnaireById(idQuestion);
    }
    public List<DataBaseQuestionnaire> getAll() {
        return dataBaseQuestionnaireRepository.getAll();
    }

    public List<Integer> iDQuestionByThemeAndLevel(String theme, String level) {
        return dataBaseQuestionnaireRepository.findAllByIdByThemeAndLevel(theme, level);
    }


}
