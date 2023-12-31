package com.argus.service.impl;

import com.argus.controller.UpdateProcessor;
import com.argus.service.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.argus.RabitQueue.ANSWER_MESSAGE;

@Service
public class AnswerConsumerImpl  implements AnswerConsumer {

    private final UpdateProcessor updateProcessor;

    public AnswerConsumerImpl(UpdateProcessor updateProcessor) {
        this.updateProcessor = updateProcessor;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consumer(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }
}
