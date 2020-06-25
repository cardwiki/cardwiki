package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.RevisionMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class CardMappingTest extends TestDataGenerator {
    @Autowired
    private RevisionMapper revisionMapper;

    @Test
    public void givenRevision_whenMapToRevisionDtoWithDeck_thenDtoHasAllProperties() {
        Revision revision = getSampleRevision();
        RevisionDto dto = revisionMapper.revision_to_revisionDtoWithDeck(revision);

        assertAll(
            () -> assertEquals(revision.getId(), dto.getId()),
            () -> assertEquals(revision.getMessage(), dto.getMessage()),
            () -> assertEquals(revision.getCreatedAt(), dto.getCreatedAt()),
            () -> assertEquals(revision.getType(), dto.getType())
        );
    }

    @Test
    public void givenRevisionInputDto_whenMapToRevision_thenRevisionHasAllProperties() {
        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront("front text");
        dto.setTextBack("back text");
        dto.setMessage("my message");

        RevisionEdit revision = revisionMapper.revisionEditDtoToRevisionEdit(dto);

        assertAll(
            () -> assertEquals(dto.getMessage(), revision.getMessage()),
            () -> assertEquals(dto.getTextFront(), revision.getTextFront()),
            () -> assertEquals(dto.getTextBack(), revision.getTextBack())
        );
    }

    @Test
    public void givenCard_whenMapToCardContentDto_thenDtoHasAllProperties() {
        Card card = new Card();
        card.setId(1L);
        RevisionEdit revisionEdit = new RevisionEdit();
        revisionEdit.setTextFront("fronti");
        revisionEdit.setTextBack("backi");
        revisionEdit.setCard(card);

        CardContentDto dto = revisionMapper.revisionEditToCardContentDto(revisionEdit);

        assertAll(
            () -> assertEquals(revisionEdit.getCard().getId(), dto.getId()),
            () -> assertEquals(revisionEdit.getTextFront(), dto.getTextFront()),
            () -> assertEquals(revisionEdit.getTextBack(), dto.getTextBack())
        );
    }
}
