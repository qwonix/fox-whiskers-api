package ru.qwonix.foxwhiskersapi.config;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Validated
@Getter
@Slf4j
@Configuration
@ConfigurationProperties("jwt")
public class JwtTokenProperties {

    /**
     * Access token expiration time.
     *
     * @see <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.typesafe-configuration-properties.conversion.durations">Spring Converting Durations Guide</a>
     */
    @DurationUnit(ChronoUnit.HOURS)
    private Duration accessTtl = Duration.ofHours(1);

    /**
     * Refresh token expiration time.
     *
     * @see <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.typesafe-configuration-properties.conversion.durations">Spring Converting Durations Guide</a>
     */
    @DurationUnit(ChronoUnit.HOURS)
    private Duration refreshTtl = Duration.ofDays(1);

    /**
     * Access Secret key for generating and verifying JWT signatures using the HMAC-SHA algorithm.
     * <p>
     * Throws WeakKeyException if the key byte array length is less than 256 bits (32 bytes) as mandated by the
     * <a href="https://tools.ietf.org/html/rfc7518#section-3.2">JWT JWA Specification
     * (RFC 7518, Section 3.2)</a>
     */
    private SecretKey accessKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * Refresh Secret key for generating and verifying JWT signatures using the HMAC-SHA algorithm.
     * <p>
     * Throws WeakKeyException if the key byte array length is less than 256 bits (32 bytes) as mandated by the
     * <a href="https://tools.ietf.org/html/rfc7518#section-3.2">JWT JWA Specification
     * (RFC 7518, Section 3.2)</a>
     */
    private SecretKey refreshKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @ConfigurationProperties("access.ttl")
    public void setAccessTtl(Duration accessTtl) {
        this.accessTtl = accessTtl;
        log.info("Access token expiration time is set at {}", accessTtl.toString());
    }

    @ConfigurationProperties("ttl.refresh")
    public void setRefreshTtl(Duration refreshTtl) {
        this.refreshTtl = refreshTtl;
        log.info("Refresh token expiration time is set at {}", refreshTtl.toString());
    }

    @ConfigurationProperties("ttl.access")
    public void setAccessKey(String accessSecret) {
        this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
        log.info("Access secret key has been accepted and successfully used");
    }

    @ConfigurationProperties("key.refresh")
    public void setRefreshKey(String refreshSecret) {
        this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecret));
        log.info("Refresh secret key has been accepted and successfully used");
    }
}