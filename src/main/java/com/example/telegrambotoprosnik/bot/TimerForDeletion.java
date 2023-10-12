package com.example.telegrambotoprosnik.bot;

import com.example.telegrambotoprosnik.model.Context;
import com.example.telegrambotoprosnik.service.ContextService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class TimerForDeletion {
    private final ContextService contextService;

    public TimerForDeletion(ContextService contextService) {
        this.contextService = contextService;
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void deleteUnusedContext(){
        if (contextService.getAllNoUsingContext().isPresent()){
        List<Context> contextList = contextService.getAllNoUsingContext().get();
            for (Context context:
                    contextList) {
                contextService.deleteByChatId(context.getChatId());
            }
        }
    }
}
