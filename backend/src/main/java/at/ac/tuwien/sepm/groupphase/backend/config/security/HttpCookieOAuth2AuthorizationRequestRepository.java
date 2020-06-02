package at.ac.tuwien.sepm.groupphase.backend.config.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    // Spring does not yet provide a Cookie implementation of AuthorizationRequestRepository
    // https://github.com/spring-projects/spring-security/issues/6374
    private static final String COOKIE_NAME = "auth";

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final boolean secureCookie;

    private final ConversionService conversionService = new DefaultConversionService();

    public HttpCookieOAuth2AuthorizationRequestRepository(boolean secureCookie) {
        this.secureCookie = secureCookie;
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getCookies() == null)
            return null;

        return Arrays.stream(httpServletRequest.getCookies()).filter(c -> c.getName().equals(COOKIE_NAME))
            .findFirst()
            .map(cookie -> {
                try {
                    // We do not use (de)serialization for untrusted data because that can lead to security vulnerabilities.

                    JsonNode node = objectMapper.reader().readTree(Base64.getDecoder().decode(cookie.getValue().getBytes()));

                    Map<String,Object> attributesMap = new HashMap<>();
                    node.get("attributes").fields().forEachRemaining(entry -> {
                        attributesMap.put(entry.getKey(), conversionService.convert(entry.getValue().asText(), Object.class));
                    });
                    Map<String,Object> additionalParamsMap = new HashMap<>();
                    node.get("additionalParams").fields().forEachRemaining(entry -> {
                        additionalParamsMap.put(entry.getKey(), conversionService.convert(entry.getValue().asText(), Object.class));
                    });

                    return OAuth2AuthorizationRequest
                        .authorizationCode().authorizationUri(node.get("authorizationUri").asText())
                        .clientId(node.get("clientId").asText())
                        .redirectUri(node.get("redirectUri").asText())
                        .state(node.get("state").asText())
                        .attributes(attributesMap)
                        .additionalParameters(additionalParamsMap)
                        .build();
                } catch (IOException e) {
                    LOGGER.error("IO Exception: {}", e.getMessage());
                    return null;
                }
            }).orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest oAuth2AuthorizationRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("authorizationUri", oAuth2AuthorizationRequest.getAuthorizationUri());
        // not putting in grantType because it's always authorization_code
        node.put("clientId", oAuth2AuthorizationRequest.getClientId());
        node.put("redirectUri", oAuth2AuthorizationRequest.getRedirectUri());
        ArrayNode scopeArrayNode = node.putArray("scopes");
        oAuth2AuthorizationRequest.getScopes().forEach(scopeArrayNode::add);
        node.put("state", oAuth2AuthorizationRequest.getState());

        ObjectNode attributesNode = node.putObject("attributes");
        oAuth2AuthorizationRequest.getAttributes().forEach((key, value) -> {
            attributesNode.put(key, conversionService.convert(value, String.class));
        });

        ObjectNode additionalParamsNode = node.putObject("additionalParams");
        oAuth2AuthorizationRequest.getAdditionalParameters().forEach((key, value) -> {
            additionalParamsNode.put(key, conversionService.convert(value, String.class));
        });

        Cookie cookie = new Cookie(COOKIE_NAME, Base64.getEncoder().encodeToString(node.toString().getBytes()));
        cookie.setPath("/");
        cookie.setMaxAge(120); // expire after two minutes
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookie);
        httpServletResponse.addCookie(cookie);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest httpServletRequest) {
        return loadAuthorizationRequest(httpServletRequest);
    }
}
