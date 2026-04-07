package org.sleepless_artery.kv_spring.config.tarantool.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;


/**
 * Configuration properties for Tarantool client.
 *
 * <p>Bound to properties with the {@code tarantool} prefix.</p>
 */
@Configuration
@ConfigurationProperties(prefix = "tarantool")
@Getter @Setter
@Validated
public class TarantoolClientConfigProperties {

    @NotBlank(message = "Host must be defined")
    private String host;

    @NotNull(message = "Port must be defined")
    private int port;
}