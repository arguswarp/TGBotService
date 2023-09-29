package com.argus.service.impl;

import com.argus.service.MainService;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.argus.RabitQueue.*;

@Service
@Log4j
public class ConsumerServiceImpl implements com.argus.service.ConsumerService {
    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessage(Update update) {
        log.debug("NODE: Text message is received");

        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumeDocMessage(Update update) {
        log.debug("NODE: Text message is received");
        mainService.processDocMessage(update);
    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumePhotoMessage(Update update) {
        log.debug("NODE: Text message is received");
        mainService.processPhotoMessage(update);
    }
}
