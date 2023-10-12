package com.example.telegrambotoprosnik.repisitory;

import com.example.telegrambotoprosnik.model.Context;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface ContextRepository extends JpaRepository<Context, Long> {
    Optional<Context> findByChatId(Long chatId);

    List<Context> findAllByTimeLastUsingBefore(LocalDateTime timeLastUsing);

}
