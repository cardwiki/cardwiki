package at.ac.tuwien.sepm.groupphase.backend.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityProps {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-time}")
    private int expirationTime;

    public byte[] getSecret() {
        return secret.getBytes();
    }

    public int getExpirationTime() {
        return expirationTime;
    }
}
