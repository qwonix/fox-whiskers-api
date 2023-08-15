package ru.qwonix.foxwhiskersapi.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import ru.qwonix.foxwhiskersapi.entity.User;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;
import ru.qwonix.foxwhiskersapi.service.UserService;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class TokenAuthenticationUserDetailsService
        implements org.springframework.security.core.userdetails.AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
        log.info("TokenAuthenticationUserDetailsService loadUserDetails");
        if (token.getPrincipal() instanceof String username && token.getCredentials() instanceof String code) {
            Optional<User> optionalUser = userService.findByPhoneNumber(username);
            if (optionalUser.isPresent()) {
                var user = optionalUser.get();
                return new org.springframework.security.core.userdetails.User(username, code, user.getRole().getAuthorities());
            }
        }

        throw new UsernameNotFoundException("Principal must be of type String");
    }
}
