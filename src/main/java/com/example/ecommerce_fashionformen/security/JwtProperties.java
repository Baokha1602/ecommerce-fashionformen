package com.example.ecommerce_fashionformen.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {

    private String issuer;
    private String secret;
    private int accessTokenTtlMinutes;
    private int refreshTokenTtlDays;
}
