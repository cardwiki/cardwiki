package at.ac.tuwien.sepm.groupphase.backend.security;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SecurityProperties securityProperties;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, SecurityProperties securityProperties) {
        super(authenticationManager);
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        try {
            UsernamePasswordAuthenticationToken authToken = getAuthToken(request);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (IllegalArgumentException | JwtException e) {
            LOGGER.debug("Invalid authorization attempt: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid authorization header or token");
            return;
        }
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthToken(HttpServletRequest request)
        throws JwtException, IllegalArgumentException {
        String token = request.getHeader(securityProperties.getAuthHeader());
        if (token == null || token.isEmpty() || !token.startsWith(securityProperties.getAuthTokenPrefix()))
            throw new IllegalArgumentException("Authorization header is malformed or missing");

        byte[] signingKey = securityProperties.getJwtSecret().getBytes();

        if (!token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token must start with 'Bearer'");
        }
        Claims claims = Jwts.parser()
            .setSigningKey(signingKey)
            .parseClaimsJws(token.replace(securityProperties.getAuthTokenPrefix(), ""))
            .getBody();

        String username = claims.getSubject();

        List<SimpleGrantedAuthority> authorities = ((List<?>) claims
            .get("rol")).stream()
            .map(authority -> new SimpleGrantedAuthority((String) authority))
            .collect(Collectors.toList());

        if (username == null || username.isEmpty()) throw new IllegalArgumentException("Token contains no user");

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
}
