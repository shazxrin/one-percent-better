package io.github.shazxrin.onepercentbetter.coach.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.shazxrin.onepercentbetter.coach.exception.CoachException;
import io.github.shazxrin.onepercentbetter.coach.model.CoachReminder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class MainCoachService implements CoachService {
    private static final Logger log = LoggerFactory.getLogger(MainCoachService.class);

    private static final String SYSTEM_PROMPT = """
        You are a life coach for Software Engineers under the program called 'One Percent Better'.
        The program encourages them to commit code every day.
        As a coach, you will be providing them encouraging quotes and reminders, similar to Duolingo.
        Make it causal, wacky and fun.
        """;
    private static final String REMINDER_USER_PROMPT = """
        Create a reminder for the user. The user committed %d commits today and streak as of today is %d.
        Please provide it in the following JSON format:
        {
            "title": string,
            "body": string
        }
        Please make the title and body of reminder fit in one sentence.
        """;

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public MainCoachService(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder
            .defaultSystem(SYSTEM_PROMPT)
            .build();
        this.objectMapper = objectMapper;
    }

    @Override
    public CoachReminder promptReminder(int commitsToday, int streakToday) {
        log.info("Prompting a reminder for user with commits today {} and streak today {}.", commitsToday, streakToday);
        String userPrompt = String.format(REMINDER_USER_PROMPT, commitsToday, streakToday);

        String reminderJson = chatClient.prompt(userPrompt)
            .call()
            .content();

        CoachReminder reminder;
        try {
            reminder = objectMapper.readValue(reminderJson, CoachReminder.class);
        } catch (JsonProcessingException ex) {
            log.error("Error parsing reminder json.", ex);

            throw new CoachException("Error parsing reminder json.", ex);
        }

        return reminder;
    }
}
