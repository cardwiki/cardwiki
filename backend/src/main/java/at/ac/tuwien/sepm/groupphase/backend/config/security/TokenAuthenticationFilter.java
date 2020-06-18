package at.ac.tuwien.sepm.groupphase.backend.config.security;

import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Collection;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private UserService userService;

    private SecurityProps securityProps;

    public TokenAuthenticationFilter(UserService userService, SecurityProps securityProps){
        this.userService = userService;
        this.securityProps = securityProps;
    }

    private static final String PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith(PREFIX)){
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(securityProps.getSecret()).build();
            Claims claims;
            try {
                claims = jwtParser.parseClaimsJws(token.substring(PREFIX.length())).getBody();
            } catch (JwtException e){
                LOGGER.info("Invalid authorization attempt: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid authorization token");
                return;
            }

            Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");

            if (claims != null){
                userService.findUserByAuthId(claims.getSubject()).ifPresent(user -> {
                    if (user.isEnabled()){
                        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

                        if (user.isAdmin())
                            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    }
                });
            }

            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities));
        }
        filterChain.doFilter(request, response);
    }
}
