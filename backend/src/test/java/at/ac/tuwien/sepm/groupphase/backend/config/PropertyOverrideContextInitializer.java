package at.ac.tuwien.sepm.groupphase.backend.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PropertyOverrideContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        Path tempDirectory;
        try {
            tempDirectory = Files.createTempDirectory(Paths.get("."), "test-images");
        } catch (IOException e) {
            throw new RuntimeException("Could not create temporary test-images directory");
        }
        tempDirectory.toFile().deleteOnExit();

        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
            configurableApplicationContext, "cawi.image-saved-path=" + tempDirectory.toString().replace('\\', '/'));
    }
}
