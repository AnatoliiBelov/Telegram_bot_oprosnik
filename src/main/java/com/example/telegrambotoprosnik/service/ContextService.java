package com.example.telegrambotoprosnik.service;

import com.example.telegrambotoprosnik.model.Context;
import com.example.telegrambotoprosnik.repisitory.ContextRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
public class ContextService {
    private final ContextRepository contextRepository;

    public ContextService(ContextRepository contextRepository) {
        this.contextRepository = contextRepository;
    }
    public void saveContext(Context context) {
        contextRepository.save(context);
    }
    public void deleteByChatId(Long chatId){
       contextRepository.deleteById(chatId);
    }

    public Optional<Context> getByChatId(Long chatId) {

        return contextRepository.findByChatId(chatId);
    }
    public Optional<List<Context>> getAllNoUsingContext(){
        LocalDateTime time = LocalDateTime.now().minusHours(1);
        return Optional.ofNullable(contextRepository.findAllByTimeLastUsingBefore(time));
    }
}
