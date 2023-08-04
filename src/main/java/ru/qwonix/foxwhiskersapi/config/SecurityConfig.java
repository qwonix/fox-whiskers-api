package ru.qwonix.foxwhiskersapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.qwonix.foxwhiskersapi.security.JwtAuthenticationProvider;

import java.time.Duration;


@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationProvider authenticationProvider,
                                           @Value("${jwt.ttl.access}") Duration accessTtl,
                                           @Value("${jwt.ttl.refresh}") Duration refreshTtl) throws Exception {
        http
                //
                .csrf(AbstractHttpConfigurer::disable)
                // REST don't create session
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // disable page caching
                .headers(headers -> headers.cacheControl(cacheControlConfig -> { }))
                .authorizeHttpRequests(authorizeHttpRequests -> {
                    authorizeHttpRequests.
                            requestMatchers("/api/v1/auth/**").permitAll().
                            requestMatchers("/error").permitAll().
//                            requestMatchers("/api/v1/dish/**").permitAll().
//                            requestMatchers("/api/v1/location/**").permitAll().
//                            requestMatchers("/api/v1/image/**").permitAll().
//                            requestMatchers("/api/v1/**").authenticated()
                                    anyRequest().authenticated();
                })
                // custom JWT based security filter
                .apply(new JwtConfigurer(authenticationProvider))
                .setAccessTtl(accessTtl)
                .setRefreshTtl(refreshTtl);

        return http.build();
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(@Value("${jwt.key.access}") String jwtAccessSecret,
                                                               @Value("${jwt.key.refresh}") String jwtRefreshSecret) {
        return new JwtAuthenticationProvider(jwtAccessSecret, jwtRefreshSecret);
    }

}
