package io.github.shazxrin.onepercentbetter.notification.service;

import io.github.shazxrin.backbone.notification.NotificationMessage;
import io.github.shazxrin.backbone.notification.NotificationMessageQueue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MainNotificationService implements NotificationService {
    private final static String ROUTING_KEY = "";
    private final RabbitTemplate rabbitTemplate;

    public MainNotificationService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendNotification(String title, String message) {
        NotificationMessage notificationMessage = new NotificationMessage(
            "one-percent-better",
            title,
            message
        );

        rabbitTemplate.convertAndSend(NotificationMessageQueue.EXCHANGE_NAME, ROUTING_KEY, notificationMessage);
    }
}
