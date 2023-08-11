package ru.qwonix.foxwhiskersapi.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class RequestTokensFilter extends OncePerRequestFilter {

    private RequestMatcher requestMatcher;
    private final AuthenticationService authenticationService;
    private final SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("RequestTokensFilter doFilterInternal");
        if (this.requestMatcher.matches(request)) {
            if (this.securityContextRepository.containsContext(request)) {
                var context = this.securityContextRepository.loadDeferredContext(request).get();
                if (context != null) {
                    Authentication authentication = context.getAuthentication();
                    if (authentication instanceof PreAuthenticatedAuthenticationToken &&
                        authentication.getPrincipal() instanceof User user) {
                        final String subject = user.getUsername();
                        List<String> authorities = authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList();
                        var accessToken = this.authenticationService.generateAccessToken(subject, authorities);
                        var refreshToken = this.authenticationService.generateRefreshToken(subject);

                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        this.objectMapper.writeValue(response.getWriter(), Map.of("accessToken", accessToken, "refreshToken", refreshToken));
                        return;
                    }
                }
            }

            throw new AccessDeniedException("User must be authenticated");
        }

        filterChain.doFilter(request, response);
    }

    public void setRequestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }
}
