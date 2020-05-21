package at.ac.tuwien.sepm.groupphase.backend.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.SerializationUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    public static final String COOKIE_NAME = "auth";
    private static final String HMAC_SHA512 = "HmacSHA512";
    private Mac sha512Hmac;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public HttpCookieOAuth2AuthorizationRequestRepository(SecurityProps securityProps) {

        try {
            final byte[] byteKey = securityProps.getSecret();
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
            sha512Hmac = Mac.getInstance(HMAC_SHA512);
            sha512Hmac.init(keySpec);

        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            LOGGER.error("Could not init SHA512MAC: {}", e.getMessage());
        }
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getCookies() == null)
            return null;

        return Arrays.stream(httpServletRequest.getCookies()).filter(c -> c.getName().equals(COOKIE_NAME))
            .findFirst()
            .filter(cookie -> Base64.getUrlEncoder().encodeToString(sha512Hmac.doFinal(cookie.getValue().split("\\.")[0].getBytes())).equals(cookie.getValue().split("\\.")[1]))
            .map(cookie -> (OAuth2AuthorizationRequest) SerializationUtils
                .deserialize(Base64.getUrlDecoder().decode(cookie.getValue().split("\\.")[0])))
            .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest oAuth2AuthorizationRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String oauth2AuthRequestCookie = Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(oAuth2AuthorizationRequest));
        Cookie cookie = new Cookie(COOKIE_NAME, oauth2AuthRequestCookie + '.' + Base64.getUrlEncoder().encodeToString(sha512Hmac.doFinal(oauth2AuthRequestCookie.getBytes())));
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest httpServletRequest) {
        return loadAuthorizationRequest(httpServletRequest);
    }
}
