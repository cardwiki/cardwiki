package at.ac.tuwien.sepm.groupphase.backend.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityProps {
    private final String secret;

    private final int expirationTime;

    private final String frontendSuccessURL;

    @Autowired
    public SecurityProps(
        @Value("${cawi.jwt-secret}") String secret,
        @Value("${cawi.jwt-expiration-time}") int expirationTime,
        @Value("${cawi.frontend-success-url}") String frontendSuccessURL){
        this.secret = secret;
        this.expirationTime = expirationTime;
        this.frontendSuccessURL = frontendSuccessURL;
    }


    public byte[] getSecret() {
        return secret.getBytes();
    }

    public int getExpirationTime() {
        return expirationTime;
    }

    public String getFrontendSuccessURL() {
        return frontendSuccessURL;
    }
}
