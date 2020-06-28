package at.ac.tuwien.sepm.groupphase.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ImageResourceConfiguration implements WebMvcConfigurer {
    private String imageSavedPath;
    private String imageServedPath;

    public ImageResourceConfiguration(@Value("${cawi.image-saved-path}") String imageSavedPath,
        @Value("${cawi.image-served-path}") String imageServedPath) {

        this.imageSavedPath = imageSavedPath;
        this.imageServedPath = imageServedPath;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(imageServedPath + "/*")
            .addResourceLocations("file:" + imageSavedPath + "/");
    }
}
