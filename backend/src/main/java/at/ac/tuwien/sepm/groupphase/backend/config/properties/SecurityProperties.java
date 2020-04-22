package at.ac.tuwien.sepm.groupphase.backend.config.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * This configuration class offers all necessary security properties in an immutable manner
 */
@Configuration
public class SecurityProperties {

    // In this case we need field injection since constructor inject would cause circular dependencies
    @Autowired
    private Auth auth;
    @Autowired
    private Jwt jwt;

    public String getAuthHeader() {
        return auth.getHeader();
    }

    public String getAuthTokenPrefix() {
        return auth.getPrefix();
    }

    public String getLoginUri() {
        return auth.getLoginUri();
    }

    public List<String> getWhiteList() {
        return auth.getWhiteList();
    }

    public String getJwtSecret() {
        return jwt.getSecret();
    }

    public String getJwtType() {
        return jwt.getType();
    }

    public String getJwtIssuer() {
        return jwt.getIssuer();
    }

    public String getJwtAudience() {
        return jwt.getAudience();
    }

    public Long getJwtExpirationTime() {
        return jwt.getExpirationTime();
    }

    @Bean
    @ConfigurationProperties(prefix = "security.auth")
    protected Auth auth() {
        return new Auth();
    }

    @Bean
    @ConfigurationProperties(prefix = "security.jwt")
    protected Jwt jwt() {
        return new Jwt();
    }

    protected class Auth {
        private String header;
        private String prefix;
        private String loginUri;
        private List<String> whiteList;

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getLoginUri() {
            return loginUri;
        }

        public void setLoginUri(String loginUri) {
            this.loginUri = loginUri;
        }

        public List<String> getWhiteList() {
            return whiteList;
        }

        public void setWhiteList(List<String> whiteList) {
            this.whiteList = whiteList;
        }
    }

    protected class Jwt {
        private String secret;
        private String type;
        private String issuer;
        private String audience;
        private Long expirationTime;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }

        public Long getExpirationTime() {
            return expirationTime;
        }

        public void setExpirationTime(Long expirationTime) {
            this.expirationTime = expirationTime;
        }
    }
}
