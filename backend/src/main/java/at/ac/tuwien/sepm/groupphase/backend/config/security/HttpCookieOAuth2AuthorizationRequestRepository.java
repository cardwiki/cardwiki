package at.ac.tuwien.sepm.groupphase.backend.config.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandles;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Base64;

public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    public static final String COOKIE_NAME = "auth";

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final byte[] secret;

    public HttpCookieOAuth2AuthorizationRequestRepository(SecurityProps props){
        secret = props.getSecret();
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getCookies() == null)
            return null;

        return Arrays.stream(httpServletRequest.getCookies()).filter(c -> c.getName().equals(COOKIE_NAME))
            .findFirst()
            .map(cookie -> {
                try {
                    JWEObject obj = JWEObject.parse(cookie.getValue());
                    obj.decrypt(new DirectDecrypter(secret));
                    return (OAuth2AuthorizationRequest) SerializationUtils.deserialize(Base64.getUrlDecoder().decode(obj.getPayload().toString()));
                } catch (ParseException e) {
                    LOGGER.error("Could not parse cookie value: {}", e.getMessage());
                    return null;
                } catch (JOSEException e) {
                    LOGGER.error("Could not decrypt cookie: {}", e.getMessage());
                    return null;
                }
            }).orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest oAuth2AuthorizationRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        JWEObject jweObject = new JWEObject(new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A256CBC_HS512),
            new Payload(Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(oAuth2AuthorizationRequest))));
        try {
            jweObject.encrypt(new DirectEncrypter(secret));
        } catch (JOSEException e) {
            LOGGER.error("Could not encrypt cookie: {}", e.getMessage());
        }

        Cookie cookie = new Cookie(COOKIE_NAME, jweObject.serialize());
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest httpServletRequest) {
        return loadAuthorizationRequest(httpServletRequest);
    }
}
