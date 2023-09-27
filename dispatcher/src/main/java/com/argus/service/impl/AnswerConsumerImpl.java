package com.argus.service.impl;

import com.argus.controller.UpdateController;
import com.argus.service.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.argus.RabitQueue.ANSWER_MESSAGE;

@Service
public class AnswerConsumerImpl  implements AnswerConsumer {

    private final UpdateController updateController;

    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consumer(SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }
}
