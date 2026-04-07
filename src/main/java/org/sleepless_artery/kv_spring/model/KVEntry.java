package org.sleepless_artery.kv_spring.model;

import jakarta.validation.constraints.NotNull;


/**
 * Key-value entry stored in the KV storage.
 */
public record KVEntry (

        @NotNull(message = "Key cannot be null")
        String key,

        byte[] value
) {}