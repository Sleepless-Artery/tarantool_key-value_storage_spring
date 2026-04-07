package org.sleepless_artery.kv_spring.config.tarantool;

import io.tarantool.client.box.TarantoolBoxClient;
import io.tarantool.client.factory.TarantoolFactory;
import lombok.RequiredArgsConstructor;
import org.sleepless_artery.kv_spring.config.tarantool.properties.TarantoolClientConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Configuration class for creating a {@link TarantoolBoxClient}.
 *
 * <p>Initializes and provides a Tarantool client bean using the connection
 * parameters defined in {@link TarantoolClientConfigProperties}.</p>
 */
@Configuration
@RequiredArgsConstructor
public class TarantoolClientConfig {

    private final TarantoolClientConfigProperties properties;

    @Bean
    public TarantoolBoxClient tarantoolClient() throws Exception {
        return TarantoolFactory.box()
                .withHost(properties.getHost())
                .withPort(properties.getPort())
                .build();
    }
}