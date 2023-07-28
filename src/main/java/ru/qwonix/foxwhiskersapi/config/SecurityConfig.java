package ru.qwonix.foxwhiskersapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import ru.qwonix.foxwhiskersapi.security.JwtAuthenticationFilter;
import ru.qwonix.foxwhiskersapi.security.JwtAuthenticationProvider;

import java.time.Duration;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationFilter authenticationTokenFilter,
                                           JwtAuthenticationProvider jwtAuthenticationProvider) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                // REST don't create session
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .securityMatchers(requestMatcherConfigurer -> {
//                    requestMatcherConfigurer
//                            .antMatchers("/api/v1/auth/**").permitAll()
//                            .antMatchers("/api/v1/dish/**").permitAll()
//                            .antMatchers("/api/v1/location/**").permitAll()
//                            .antMatchers("/api/v1/image/**").permitAll()
//                            .antMatchers("/api/v1/**").authenticated()
//                            .anyRequest().permitAll()
//                })
//
                // custom JWT based security filter
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // custom JWT based security filter
                .authenticationProvider(jwtAuthenticationProvider);


        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        return authenticationManagerBuilder.build();
    }

    @Bean
    public JwtAuthenticationFilter authenticationTokenFilter(AuthenticationManager authenticationManager) {
        return new JwtAuthenticationFilter("/api/v1/**", authenticationManager,
                new OrRequestMatcher(
                        new AntPathRequestMatcher("/api/v1/auth/**"),
                        new AntPathRequestMatcher("/api/v1/dish/**"),
                        new AntPathRequestMatcher("/api/v1/location/**"),
                        new AntPathRequestMatcher("/api/v1/client/**"),
                        new AntPathRequestMatcher("/api/v1/image/**")
                )
        );
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(
            @Value("${jwt.key.access}") String jwtAccessSecret,
            @Value("${jwt.key.refresh}") String jwtRefreshSecret,
            @Value("${jwt.ttl.access}") Duration accessTtl,
            @Value("${jwt.ttl.refresh}") Duration refreshTtl) {
        JwtAuthenticationProvider jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtAccessSecret, jwtRefreshSecret);
        jwtAuthenticationProvider.setAccessTtl(accessTtl);
        jwtAuthenticationProvider.setRefreshTtl(refreshTtl);
        return jwtAuthenticationProvider;
    }
}
