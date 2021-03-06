package at.ac.tuwien.sepm.groupphase.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RestController;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

    @Bean
    public Docket apiDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(new ApiInfo(
                "Backend of CardWiki",
                "Interactive API documentation for the backend",
                "v1",
                null,
                null,
                null,
                null,
                Collections.emptyList()
            ))
            .securitySchemes(apiKeys());
    }

    private List<ApiKey> apiKeys() {
        // user/admin is the name of the token, `Authorization` is the key in the request header
        return Arrays.asList(
            new ApiKey("user", "Authorization", "header"),
            new ApiKey("admin", "Authorization", "header")
        );
    }
}
