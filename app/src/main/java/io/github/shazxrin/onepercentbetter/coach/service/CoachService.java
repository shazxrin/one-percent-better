package io.github.shazxrin.onepercentbetter.coach.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.shazxrin.onepercentbetter.coach.exception.CoachException;
import io.github.shazxrin.onepercentbetter.coach.model.CoachReminder;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Observed
@Service
public class CoachService {
    private static final Logger log = LoggerFactory.getLogger(CoachService.class);

    private static final String SYSTEM_PROMPT = """
        You are a life coach for Software Engineers under the program called 'One Percent Better'.
        The program encourages them to be a better version of themselves by committing code every day.
        The Software Engineers are hoping to be able to see themselves level up in their career.
        As a coach, you will be providing them encouraging quotes and reminders, similar to Duolingo.
        Make it causal, wacky and fun.
        """;
    private static final String REMINDER_USER_PROMPT = """
        Create a reminder for the user on their progress for today.
        As of %s hours, the user has committed %d commits today and their streak as of today is %d days.
        Please provide it in the following JSON format:
        {
            "title": "Title of reminder",
            "body": "Contents of reminder"
        }
        Please make the title fit in one sentence.
        """;
    private static final Pattern MD_JSON_REGEX_PATTERN = Pattern.compile("```json\\s*([\\s\\S]*?)\\s*```");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public CoachService(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder
            .defaultSystem(SYSTEM_PROMPT)
            .build();
        this.objectMapper = objectMapper;
    }

    public CoachReminder promptReminder(int commitsToday, int streakToday) {
        log.info("Prompting a reminder for user with commits today {} and streak today {}.", commitsToday, streakToday);
        String userPrompt = String.format(
            REMINDER_USER_PROMPT,
            LocalTime.now().format(TIME_FORMATTER),
            commitsToday,
            streakToday
        );

        String reminderJson = chatClient.prompt(userPrompt)
            .call()
            .content();

        if (reminderJson == null) {
            log.error("No response from coach.");
            throw new CoachException("No response from coach.");
        }
        
        // We need to cleanse the response because Gemini wraps it in a Markdown code block
        String cleanReminderJson;
        Matcher matcher = MD_JSON_REGEX_PATTERN.matcher(reminderJson);
        if (matcher.find()) {
            cleanReminderJson = matcher.group(1).trim();
        } else {
            throw new CoachException("Cannot find reminder from coach.");
        }

        CoachReminder reminder;
        try {
            reminder = objectMapper.readValue(cleanReminderJson, CoachReminder.class);
        } catch (JsonProcessingException ex) {
            log.error("Error parsing reminder json.", ex);

            throw new CoachException("Error parsing reminder json.", ex);
        }

        return reminder;
    }
}
