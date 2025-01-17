package com.beta.postandcomments.application.adapters.bus;


import co.com.sofka.domain.generic.DomainEvent;
import com.beta.postandcomments.application.config.RabbitMqConfig;
import com.beta.postandcomments.business.gateways.EventBus;
import com.google.gson.Gson;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class RabbitMqEventBus implements EventBus {
    private final RabbitTemplate rabbitTemplate;
    private final Gson gson = new Gson();

    public RabbitMqEventBus(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(DomainEvent event) {
        var notification = new Notification(
                event.getClass().getTypeName(),
                gson.toJson(event));
        //Find a way to send this notification through the predefined queues in the rabbitMq configuration,
        //To that specific exchange and queues bases on the type of event
        sendNotification(notification);



    }
    public void sendNotification (Notification notification){
        if (notification.getType().contains("PostCreated")) {
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.PROXY_ROUTING_KEY_POST_CREATED, notification.serialize().getBytes());
        } else {
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.PROXY_ROUTING_KEY_COMMENT_ADDED, notification.serialize().getBytes());
        }
    }

    @Override
    public void publishError(Throwable errorEvent) {

    }
}
