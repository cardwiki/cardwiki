package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CardMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class CardMappingTest extends TestDataGenerator {
    @Autowired
    private CardMapper cardMapper;

    @Test
    public void givenCard_whenMapToCardDetailsDto_thenDtoHasAllProperties() {
        Card card = getSampleRevisionEdit().getRevision().getCard();

        CardDetailsDto dto = cardMapper.cardToCardDetailsDto(card);

        assertAll(
            () -> assertEquals(card.getId(), dto.getId()),
            () -> assertEquals(card.getLatestRevision().getRevisionEdit().getTextFront(), dto.getTextFront()),
            () -> assertEquals(card.getLatestRevision().getRevisionEdit().getTextBack(), dto.getTextBack()),
            () -> assertEquals(card.getDeck().getId(), dto.getDeck().getId()),
            () -> assertEquals(card.getDeck().getName(), dto.getDeck().getName()),
            () -> assertIterableEquals(
                card.getRevisions().stream()
                    .map(cardMapper::revisionToRevisionSimpleDto)
                    .collect(Collectors.toList()),
                dto.getRevisions()
            )
        );
    }

    @Test
    public void givenRevision_whenMapToRevisionSimpleDto_thenDtoHasAllProperties() {
        Revision revision = getSampleRevision();
        RevisionSimpleDto dto = cardMapper.revisionToRevisionSimpleDto(revision);

        assertAll(
            () -> assertEquals(revision.getId(), dto.getId()),
            () -> assertEquals(revision.getMessage(), dto.getMessage()),
            () -> assertEquals(revision.getCreatedBy().getId(), dto.getCreatedBy()),
            () -> assertEquals(revision.getCreatedAt(), dto.getCreatedAt())
        );
    }

    @Test
    public void givenRevisionEditInquiryDto_whenMapToRevisionEdit_thenRevisionEditHasAllProperties() {
        RevisionEditInquiryDto dto = new RevisionEditInquiryDto();
        dto.setTextFront("front text");
        dto.setTextBack("back text");

        RevisionEdit revisionEdit = cardMapper.revisionEditInquiryDtoToRevisionEdit(dto);

        assertAll(
            () -> assertEquals(dto.getTextFront(), revisionEdit.getTextFront()),
            () -> assertEquals(dto.getTextBack(), revisionEdit.getTextBack())
        );
    }


}
