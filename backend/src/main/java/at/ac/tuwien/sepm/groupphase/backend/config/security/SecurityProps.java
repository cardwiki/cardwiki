package at.ac.tuwien.sepm.groupphase.backend.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityProps {
    private final String secret;

    private final int expirationTime;

    private final boolean secureCookies;

    @Autowired
    public SecurityProps(
        @Value("${cawi.jwt-secret}") String secret,
        @Value("${cawi.jwt-expiration-time}") int expirationTime,
        @Value("${cawi.secure-cookie}") boolean secureCookies
    ){
        this.secret = secret;
        this.expirationTime = expirationTime;
        this.secureCookies = secureCookies;
    }


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
