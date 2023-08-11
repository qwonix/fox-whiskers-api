package ru.qwonix.foxwhiskersapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.qwonix.foxwhiskersapi.repository.AuthenticationRepository;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;
import ru.qwonix.foxwhiskersapi.service.ClientService;
import ru.qwonix.foxwhiskersapi.service.impl.JwtAuthenticationService;


@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtConfigurer jwtConfigurer) throws Exception {
        http.apply(jwtConfigurer);

        http
                // fixme: not good
                 .csrf(AbstractHttpConfigurer::disable)
                // REST don't create session
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // disable page caching
                .headers(headers -> headers.cacheControl(cacheControlConfig -> {
                }))
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers("/error").permitAll()
                                .requestMatchers("/api/v1/auth/code").permitAll()
                                .requestMatchers("/api/v1/dish/**").permitAll()
                                .requestMatchers("/api/v1/image/**").permitAll()
                                .requestMatchers("/api/v1/location/**").permitAll()
                                .anyRequest().authenticated())
        ;

        return http.build();
    }

    @Bean
    public JwtConfigurer jwtConfigurer(AuthenticationService authenticationService) {

        return new JwtConfigurer(authenticationService)
                .authenticationRequestMatcher(new AntPathRequestMatcher("/api/v1/auth", HttpMethod.POST.name()));
    }

    @Bean
    public JwtAuthenticationService authenticationService(ClientService clientService,
                                                          AuthenticationRepository authenticationRepository,
                                                          @Value("${jwt.key.access}") String jwtAccessSecret,
                                                          @Value("${jwt.key.refresh}") String jwtRefreshSecret) {
        return new JwtAuthenticationService(clientService, authenticationRepository, jwtAccessSecret, jwtRefreshSecret);
    }


}
