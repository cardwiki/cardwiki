package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.config.PropertyOverrideContextInitializer;
import at.ac.tuwien.sepm.groupphase.backend.entity.Image;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.repository.ImageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.FileInputStream;
import java.nio.file.Paths;

import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.security.MockedLogins.USER_ROLES;
import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.security.MockedLogins.mockLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration(initializers = PropertyOverrideContextInitializer.class)
public class ImageEndpointTest extends TestDataGenerator {

    @Autowired
    private MockMvc mvc;

    @Value("${cawi.image-saved-path}")
    private String imageSavedPath;

    @Value("${cawi.image-served-path}")
    private String imageServedPath;

    @Autowired
    private ImageRepository imageRepository;

    private static final String testImagePath = "src/test/resources/test.png";
    private static final String testImageHash = "b18ce9558fe729bcc7edcf20d8a81f12c0637359df5f930370ca5fff072ee175.png";
    private static final String unsupportedTestImagePath = "src/test/resources/badrequest.gif";

    @Test
    public void uploadImageReturnsImageDto() throws Exception {
        User user = givenApplicationUser();
        FileInputStream fileInputStream = new FileInputStream(testImagePath);
        MockMultipartFile multipartFile = new MockMultipartFile("file", fileInputStream);

        String imagePath = Paths.get(imageServedPath, testImageHash).toString().replace('\\', '/');

        mvc.perform(multipart("/api/v1/images")
            .file(multipartFile)
            .with(mockLogin(USER_ROLES, user.getAuthId())))
            .andExpect(status().is(201))
            .andExpect(jsonPath("$.filename").value(testImageHash))
            .andExpect(jsonPath("$.url").value(imagePath));

        mvc.perform(get(imagePath)).andExpect(status().isOk());

        Paths.get(imageSavedPath, testImageHash).toFile().delete();
    }

    @Test
    public void uploadImageWithUnsupportedContentTypeThrowsBadRequest() throws Exception {
        User user = givenApplicationUser();
        FileInputStream fileInputStream = new FileInputStream(unsupportedTestImagePath);
        MockMultipartFile multipartFile = new MockMultipartFile("file", fileInputStream);

        mvc.perform(multipart("/api/v1/images")
            .file(multipartFile)
            .with(mockLogin(USER_ROLES, user.getAuthId())))
            .andExpect(status().is(400));
    }

    @Test
    public void uploadFileWhoseContentTypeCannotBeGuessedThrowsBadRequest() throws Exception {
        User user = givenApplicationUser();
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
            "text/plain", "test".getBytes());

        mvc.perform(multipart("/api/v1/images")
            .file(multipartFile)
            .with(mockLogin(USER_ROLES, user.getAuthId())))
            .andExpect(status().is(400));
    }

    @Test
    public void uploadEmptyFileThrowsBadRequest() throws Exception {
        User user = givenApplicationUser();
        MockMultipartFile multipartFile = new MockMultipartFile("file", new byte[0]);

        mvc.perform(multipart("/api/v1/images")
            .file(multipartFile)
            .with(mockLogin(USER_ROLES, user.getAuthId())))
            .andExpect(status().is(400));
    }

    @Test
    public void uploadAlreadyStoredFileReturnsExistingImage() throws Exception {
        User user = givenApplicationUser();
        Image image = new Image();
        image.setCreatedBy(user);
        image.setFilename(testImageHash);
        image = imageRepository.save(image);

        FileInputStream fileInputStream = new FileInputStream(testImagePath);
        MockMultipartFile multipartFile = new MockMultipartFile("file", fileInputStream);

        mvc.perform(multipart("/api/v1/images")
            .file(multipartFile)
            .with(mockLogin(USER_ROLES, user.getAuthId())))
            .andExpect(status().is(201))
            .andExpect(jsonPath("$.filename").value(testImageHash))
            .andExpect(jsonPath("$.url").value(Paths.get(imageServedPath, testImageHash).toString().replace('\\', '/')));

        Paths.get(imageSavedPath, testImageHash).toFile().delete();
    }

}
