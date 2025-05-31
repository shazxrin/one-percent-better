package io.github.shazxrin.onepercentbetter.notification.service;

import io.github.shazxrin.backbone.notification.NotificationMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MainNotificationService implements NotificationService {
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

        rabbitTemplate.convertAndSend("notification", notificationMessage);
    }
}
