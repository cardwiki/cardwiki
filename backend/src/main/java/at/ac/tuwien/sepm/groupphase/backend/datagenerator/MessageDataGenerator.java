package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

@Profile("generateData")
@Component
public class MessageDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_MESSAGES_TO_GENERATE = 5;
    private static final String TEST_NEWS_TITLE = "Title";
    private static final String TEST_NEWS_SUMMARY = "Summary of the message";
    private static final String TEST_NEWS_TEXT = "This is the text of the message";

    private final MessageRepository messageRepository;

    public MessageDataGenerator(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @PostConstruct
    private void generateMessage() {
        if (messageRepository.findAll().size() > 0) {
            LOGGER.debug("message already generated");
        } else {
            LOGGER.debug("generating {} message entries", NUMBER_OF_MESSAGES_TO_GENERATE);
            for (int i = 0; i < NUMBER_OF_MESSAGES_TO_GENERATE; i++) {
                Message message = Message.MessageBuilder.aMessage()
                    .withTitle(TEST_NEWS_TITLE + " " + i)
                    .withSummary(TEST_NEWS_SUMMARY + " " + i)
                    .withText(TEST_NEWS_TEXT + " " + i)
                    .withPublishedAt(LocalDateTime.now().minusMonths(i))
                    .build();
                LOGGER.debug("saving message {}", message);
                messageRepository.save(message);
            }
        }
    }

}
