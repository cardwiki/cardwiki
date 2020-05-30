package at.ac.tuwien.sepm.groupphase.backend.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityProps {
    @Value("${cawi.jwt-secret}")
    private String secret;

    @Value("${cawi.jwt-expiration-time}")
    private int expirationTime;

    @Value("${cawi.secure-cookie}")
    private boolean secureCookies;

    public byte[] getSecret() {
        return secret.getBytes();
    }

    public int getExpirationTime() {
        return expirationTime;
    }

    public boolean cookiesAreSecure() {
        return secureCookies;
    }
}
