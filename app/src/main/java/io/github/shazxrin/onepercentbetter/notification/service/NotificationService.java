package io.github.shazxrin.onepercentbetter.notification.service;

import io.github.shazxrin.notifier.common.NotificationMessage;
import io.github.shazxrin.notifier.common.NotificationMessageQueue;
import io.micrometer.observation.annotation.Observed;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Observed
@Service
public class NotificationService {
    private final static String APP_NAME = "one-percent-better";
    private final static String ROUTING_KEY = "";

    private final RabbitTemplate rabbitTemplate;

    public NotificationService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotification(String title, String message) {
        NotificationMessage notificationMessage = new NotificationMessage(
            APP_NAME,
            title,
            message
        );

        rabbitTemplate.convertAndSend(NotificationMessageQueue.EXCHANGE_NAME, ROUTING_KEY, notificationMessage);
    }
}
