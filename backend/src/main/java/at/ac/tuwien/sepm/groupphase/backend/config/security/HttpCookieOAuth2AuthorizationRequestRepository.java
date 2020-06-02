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
import org.springframework.util.Assert;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.*;

/** Spring does not yet provide a Cookie implementation of AuthorizationRequestRepository
 *  https://github.com/spring-projects/spring-security/issues/6374
 */
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    private static final String COOKIE_NAME =
        HttpCookieOAuth2AuthorizationRequestRepository.class.getName() +  ".AUTHORIZATION_REQUEST";

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ConversionService conversionService = new DefaultConversionService();

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Assert.notNull(request, "request cannot be null");

        if (request.getCookies() == null)
            return null;

        return Arrays.stream(request.getCookies()).filter(c -> c.getName().equals(COOKIE_NAME))
            .findFirst()
            .map(cookie -> {
                try {
                    // We do not use (de)serialization for untrusted data because that can lead to security vulnerabilities.

                    JsonNode node = objectMapper.reader().readTree(Base64.getDecoder().decode(cookie.getValue().getBytes()));

                    Map<String,Object> attributesMap = new HashMap<>();
                    node.get("attributes").fields().forEachRemaining(entry -> {
                        attributesMap.put(entry.getKey(), conversionService.convert(entry.getValue().textValue(), Object.class));
                    });
                    Map<String,Object> additionalParamsMap = new HashMap<>();
                    node.get("additionalParams").fields().forEachRemaining(entry -> {
                        additionalParamsMap.put(entry.getKey(), conversionService.convert(entry.getValue().textValue(), Object.class));
                    });
                    Set<String> scopes = new HashSet<>();
                    node.withArray("scopes").forEach(scopeNode -> scopes.add(scopeNode.textValue()));

                    return OAuth2AuthorizationRequest
                        .authorizationCode().authorizationUri(node.get("authorizationUri").textValue())
                        .authorizationRequestUri(node.get("authorizationRequestUri").textValue())
                        .clientId(node.get("clientId").textValue())
                        .redirectUri(node.get("redirectUri").textValue())
                        .state(node.get("state").textValue())
                        .scopes(scopes)
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
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(request, "request cannot be null");
        Assert.notNull(response, "response cannot be null");

        if (authorizationRequest == null){
            response.addCookie(expiredCookie(request));
            return;
        }

        ObjectNode node = objectMapper.createObjectNode();
        node.put("authorizationUri", authorizationRequest.getAuthorizationUri());
        node.put("authorizationRequestUri", authorizationRequest.getAuthorizationRequestUri());
        // not putting in grantType because it's always authorization_code
        node.put("clientId", authorizationRequest.getClientId());
        node.put("redirectUri", authorizationRequest.getRedirectUri());
        ArrayNode scopeArrayNode = node.putArray("scopes");
        authorizationRequest.getScopes().forEach(scopeArrayNode::add);
        node.put("state", authorizationRequest.getState());

        ObjectNode attributesNode = node.putObject("attributes");
        authorizationRequest.getAttributes().forEach((key, value) -> {
            attributesNode.put(key, conversionService.convert(value, String.class));
        });

        ObjectNode additionalParamsNode = node.putObject("additionalParams");
        authorizationRequest.getAdditionalParameters().forEach((key, value) -> {
            additionalParamsNode.put(key, conversionService.convert(value, String.class));
        });
        response.addCookie(buildCookie(Base64.getEncoder().encodeToString(node.toString().getBytes()), request));
    }

    private Cookie buildCookie(String value, HttpServletRequest request){
        Cookie cookie = new Cookie(COOKIE_NAME, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        cookie.setMaxAge(120); // expire after two minutes
        return cookie;
    }

    private Cookie expiredCookie(HttpServletRequest request){
        Cookie cookie = buildCookie("", request);
        cookie.setMaxAge(0);
        return cookie;
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(request, "request cannot be null");
        Assert.notNull(response, "response cannot be null");
        response.addCookie(expiredCookie(request));
        return loadAuthorizationRequest(request);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        Assert.notNull(request, "request cannot be null");
        // we cannot actually remove the authorizationRequest here because we don't have access to the httpServletResponse
        return loadAuthorizationRequest(request);
    }
}
