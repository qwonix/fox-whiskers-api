package ru.qwonix.foxwhiskersapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
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
                                           JwtAuthenticationProvider jwtAuthenticationProvider,
                                           DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
        http
                //
                .csrf().disable()
                // REST don't create session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/v1/auth/**").permitAll()
                .antMatchers("/api/v1/**").authenticated()
                .anyRequest().permitAll()
                .and()
                // custom JWT based security filter
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // custom JWT based security filter
                .authenticationProvider(jwtAuthenticationProvider)
                .authenticationProvider(daoAuthenticationProvider);

        // disable page caching
        http.headers().cacheControl();


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
        return new JwtAuthenticationFilter("/api/v1/**", authenticationManager, new AntPathRequestMatcher("/api/v1/auth/**"));
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(UserDetailsService userDetailsService,
                                                                   @Value("${jwt.secret.access}") String jwtAccessSecret,
                                                                   @Value("${jwt.secret.refresh}") String jwtRefreshSecret,
                                                                   @Value("${jwt.expiration.access}") Duration accessExpiration,
                                                                   @Value("${jwt.expiration.refresh}") Duration refreshExpiration) {
        JwtAuthenticationProvider jwtAuthenticationProvider = new JwtAuthenticationProvider(userDetailsService, jwtAccessSecret, jwtRefreshSecret);
        jwtAuthenticationProvider.setAccessExpiration(accessExpiration);
        jwtAuthenticationProvider.setRefreshExpiration(refreshExpiration);
        return jwtAuthenticationProvider;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);

        return daoAuthenticationProvider;
    }
}
