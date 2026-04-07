package org.sleepless_artery.kv_spring.repository;

import org.sleepless_artery.kv_spring.model.KVEntry;

import java.util.List;


/**
 * Repository interface for key-value storage operations.
 */
public interface KVRepository {

    void put(String key, byte[] value);

    KVEntry get(String key);

    void delete(String key);

    List<KVEntry> rangeBatch(String keySince, String keyTo, int limit);

    long count();
}