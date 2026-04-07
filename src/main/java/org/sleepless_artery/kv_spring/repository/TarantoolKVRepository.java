package org.sleepless_artery.kv_spring.repository;

import io.tarantool.client.box.TarantoolBoxClient;
import lombok.RequiredArgsConstructor;
import org.sleepless_artery.kv_spring.model.KVEntry;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


/**
 * Tarantool-based implementation of {@link KVRepository}.
 */
@Repository
@RequiredArgsConstructor
public class TarantoolKVRepository implements KVRepository {

    private final TarantoolBoxClient client;

    private static final String SPACE_NAME = "KV";


    /**
     * Inserts or updates a value by key.
     *
     * @param key   entry key
     * @param value entry value
     */
    @Override
    public void put(String key, byte[] value) {
        client.space(SPACE_NAME)
                .upsert(
                        List.of(key, value),
                        List.of(
                                List.of("=", 1, value)
                        )
                )
                .join();
    }


    /**
     * Retrieves value by key.
     *
     * @param key entry key
     * @return {@link  KVEntry} or {@code null} if entry does not exist
     */
    @Override
    public KVEntry get(String key) {
        var result = client.space(SPACE_NAME)
                .select(List.of(key))
                .join()
                .get();

        if (result == null || result.isEmpty()) {
            return null;
        }

        var tuple = result.getFirst().get();
        var foundKey = (String) tuple.getFirst();
        var value = tuple.get(1);

        if (foundKey == null) {
            return null;
        }

        return new KVEntry(foundKey, (byte[]) value);
    }


    /**
     * Deletes value by key.
     *
     * @param key entry key to delete
     */
    @Override
    public void delete(String key) {
        client.space(SPACE_NAME)
                .delete(
                        List.of(key)
                )
                .join();
    }


    /**
     * Retrieves a batch of entries in key range.
     *
     * @param keySince start key (inclusive)
     * @param keyTo    end key (inclusive)
     * @param limit    max number of entries
     * @return {@link List} list of entries ({@link KVEntry})
     */
    @Override
    public List<KVEntry> rangeBatch(String keySince, String keyTo, int limit) {
        var rawResult = client.call(
                        "kv_range",
                        List.of(keySince, keyTo, limit)
                )
                .join()
                .get();

        if (rawResult == null || rawResult.isEmpty()) {
            return List.of();
        }

        return !(rawResult.getFirst() instanceof List<?> rows)
                ? List.of()
                : getEntries(rows);
    }


    /**
     * Counts total number of stored entries.
     *
     * @return entry count
     */
    @Override
    public long count() {
        var count = client.call("kv_count")
                .join()
                .get()
                .getFirst();

        if (count instanceof Number) {
            return ((Number) count).longValue();
        }

        return 0L;
    }


    private ArrayList<KVEntry> getEntries(List<?> list) {
        var entries = new ArrayList<KVEntry>();

        for (Object item : list) {
            if (!(item instanceof List<?> tuple) || tuple.isEmpty()) {
                continue;
            }

            String key = (String) tuple.getFirst();
            Object value = tuple.size() > 1 ? tuple.get(1) : null;

            entries.add(new KVEntry(
                    key,
                    value == null ? null : (byte[]) value
            ));
        }

        return entries;
    }
}