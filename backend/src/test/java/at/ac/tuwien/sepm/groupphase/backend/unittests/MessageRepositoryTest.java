package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class MessageRepositoryTest implements TestData {

    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void givenNothing_whenSaveMessage_thenFindListWithOneElementAndFindMessageById() {
        Message message = Message.MessageBuilder.aMessage()
            .withTitle(TEST_NEWS_TITLE)
            .withSummary(TEST_NEWS_SUMMARY)
            .withText(TEST_NEWS_TEXT)
            .withPublishedAt(TEST_NEWS_PUBLISHED_AT)
            .build();

        messageRepository.save(message);

        assertAll(
            () -> assertEquals(1, messageRepository.findAll().size()),
            () -> assertNotNull(messageRepository.findById(message.getId()))
        );
    }

}
