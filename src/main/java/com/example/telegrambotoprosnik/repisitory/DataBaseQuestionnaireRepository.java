package com.example.telegrambotoprosnik.repisitory;

import com.example.telegrambotoprosnik.model.DataBaseQuestionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataBaseQuestionnaireRepository extends JpaRepository<DataBaseQuestionnaire, Long> {
    @Query(value = "SELECT d FROM DataBaseQuestionnaire d WHERE  d.id = :id")
    DataBaseQuestionnaire findDataBaseQuestionnaireById(@Param("id") int idQuestion);

    @Query(value = "SELECT d FROM DataBaseQuestionnaire d WHERE d.theme=:theme and d.level=:level")
    List<DataBaseQuestionnaire> getAllByThemeAndLevel(@Param("theme")String theme,@Param("level") String level);

    @Query(value = "SELECT d FROM DataBaseQuestionnaire d ")
    List<DataBaseQuestionnaire> getAll();

    @Query(value = "SELECT w.id from DataBaseQuestionnaire w WHERE w.theme=:theme and w.level=:level")
    List<Integer> findAllByIdByThemeAndLevel(@Param("theme")String theme,@Param("level") String level);


}
