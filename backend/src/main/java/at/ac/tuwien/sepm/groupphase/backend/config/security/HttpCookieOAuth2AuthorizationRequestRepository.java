package at.ac.tuwien.sepm.groupphase.backend.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.SerializationUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandles;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    public static final String COOKIE_NAME = "auth";
    public static final String PDKDF_ALGORITHM = "PBKDF2WithHmacSHA512" ;
    public static final int ITERATION_COUNT = 12288 ;
    public static final int SALT_LENGTH = 16 ;
    public static final int DERIVED_KEY_LENGTH = 32 ;

    private static final SecureRandom secureRandom = new SecureRandom();
    private SecretKey key;
    private final Cipher aes;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public HttpCookieOAuth2AuthorizationRequestRepository(SecurityProps securityProps) throws GeneralSecurityException {
        generateEncryptionKey(new String(securityProps.getSecret()).toCharArray());
        aes = Cipher.getInstance("AES/CTR/PKCS5Padding");
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getCookies() == null)
            return null;

        return Arrays.stream(httpServletRequest.getCookies()).filter(c -> c.getName().equals(COOKIE_NAME))
            .findFirst()
            .map(cookie -> {
                OAuth2AuthorizationRequest  oAuth2AuthorizationRequest = null;
                try {
                    IvParameterSpec iv = new IvParameterSpec(Base64.getUrlDecoder().decode(cookie.getValue().split("\\.")[1]));
                    aes.init(Cipher.DECRYPT_MODE, key, iv);
                    oAuth2AuthorizationRequest = (OAuth2AuthorizationRequest) SerializationUtils.deserialize(aes.doFinal(Base64.getUrlDecoder().decode(cookie.getValue().split("\\.")[0])));
                } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
                    LOGGER.error("Could not decrypt oauth2AuthRequestCookie: {}", e.getMessage());
                }
                return oAuth2AuthorizationRequest;})
            .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest oAuth2AuthorizationRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        try {
            byte[] ivBytes = new byte[16];
            secureRandom.nextBytes(ivBytes);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            String ivBase64 = Base64.getUrlEncoder().encodeToString(ivBytes);
            aes.init(Cipher.ENCRYPT_MODE, key, iv);

            String oauth2AuthRequestCookie = Base64.getUrlEncoder().encodeToString(aes.doFinal(Objects.requireNonNull(SerializationUtils.serialize(oAuth2AuthorizationRequest))));
            Cookie cookie = new Cookie(COOKIE_NAME, oauth2AuthRequestCookie + '.' + ivBase64);
            cookie.setPath("/");
            httpServletResponse.addCookie(cookie);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            LOGGER.error("Could not encrypt oauth2AuthRequestCookie: {}", e.getMessage());
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest httpServletRequest) {
        return loadAuthorizationRequest(httpServletRequest);
    }

    private void generateEncryptionKey(char[] keyChars) throws GeneralSecurityException {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);

        PBEKeySpec keySpec;
        try {
            keySpec = new PBEKeySpec(keyChars, salt, ITERATION_COUNT , DERIVED_KEY_LENGTH * 8);
        } catch(NullPointerException nullPointerExc){throw new GeneralSecurityException("Salt " + Arrays.toString(salt) + "is null");}
        catch(IllegalArgumentException illegalArgumentExc){throw new GeneralSecurityException("One of the argument is illegal. Salt " + Arrays.toString(salt) + " is of 0 length, iteration count " + ITERATION_COUNT + " is not positive or derived key length " + DERIVED_KEY_LENGTH + " is not positive." );}

        SecretKeyFactory pbkdfKeyFactory = null;

        try {
            pbkdfKeyFactory = SecretKeyFactory.getInstance(PDKDF_ALGORITHM);
        } catch(NullPointerException nullPointExc) {throw new GeneralSecurityException("Specified algorithm " + PDKDF_ALGORITHM  + "is null");}
        catch(NoSuchAlgorithmException noSuchAlgoExc) {throw new GeneralSecurityException("Specified algorithm " + PDKDF_ALGORITHM + "is not available by the provider " + pbkdfKeyFactory.getProvider().getName());}

        byte[] pbkdfHashedArray;
        try {
            pbkdfHashedArray = pbkdfKeyFactory.generateSecret(keySpec).getEncoded();
        } catch(InvalidKeySpecException invalidKeyExc) {throw new GeneralSecurityException("Specified key specification is inappropriate"); }

        key = new SecretKeySpec(pbkdfHashedArray, 0, pbkdfHashedArray.length, "AES");
    }
}
