package com.argus.service.impl;

import com.argus.service.ProducerService;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.argus.RabitQueue.*;

@Service
@Log4j
public class ConsumerServiceImpl implements com.argus.service.ConsumerService {
    private final ProducerService producerService;

    public ConsumerServiceImpl(ProducerService producerService) {
        this.producerService = producerService;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessage(Update update) {
        log.debug("NODE: Text message is received");

        var message = update.getMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("Hello from NODE");
        producerService.produceAnswer(sendMessage);
    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumeDocMessage(Update update) {
        log.debug("NODE: Text message is received");

    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumePhotoMessage(Update update) {
        log.debug("NODE: Text message is received");

    }
}
