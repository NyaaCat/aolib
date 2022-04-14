package cat.nyaa.aolib.database;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleKVCache<K, V> {
    public final ConcurrentHashMap<K, V> cache;
    private final simpleDataProvider<K, V> provider;
    private final AtomicInteger loaded = new AtomicInteger(0); // -1 failed, 0 not loaded, 1 loaded
    private CompletableFuture<?> loadFuture;

    public SimpleKVCache(@NotNull simpleDataProvider<K, V> provider) {
        this.provider = provider;
        this.cache = new ConcurrentHashMap<>();
        reload();
    }

    /**
     * reload all data from provider
     *
     * @return the CompletableFuture that executes the update task, or empty if failed.
     */

    public Optional<CompletableFuture<?>> reload() {
        if (loaded.get() == 0) {
            return Optional.empty();
        }
        this.loaded.set(0);
        this.loadFuture = provider.getAll()
                .thenAccept(optionalMap -> optionalMap.ifPresentOrElse((map) -> {
                    cache.putAll(map);
                    this.loaded.set(1);
                }, () -> this.loaded.set(-1)))
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    this.loaded.set(-1);
                    return null;
                });
        return Optional.ofNullable(this.loadFuture);
    }


    /**
     * @return true if cache is loaded
     */
    public boolean isLoaded() {
        return loaded.get() > 0;
    }

    /**
     * wait until cache is loaded
     */
    public void waitToLoad() {
        loadFuture.join();
    }

    private void checkLoaded() {
        if (loaded.get() == 0) {
            waitToLoad();
        }
        if (!isLoaded()) {
            throw new IllegalStateException("cache not loaded");
        }
    }

    /**
     * get value from cache
     *
     * @param k KEY
     * @return VALUE
     */
    public V get(K k) {
        checkLoaded();
        return cache.get(k);
    }

    /**
     * tests if specified key in the cache
     *
     * @param k KEY
     * @return true if key exists
     */

    public boolean containsKey(K k) {
        checkLoaded();
        return cache.containsKey(k);
    }

    /**
     * tests if specified value in the cache
     *
     * @param v VALUE
     * @return true if value exists
     */
    public boolean containsValue(V v) {
        checkLoaded();
        return cache.containsValue(v);
    }

    /**
     * get value and update cache
     *
     * @param k KEY
     * @return VALUE
     */

    public CompletableFuture<Optional<V>> getAndUpdateCache(K k) {
        checkLoaded();
        return provider.get(k).thenApply(v -> {
            v.ifPresent(v1 -> cache.put(k, v1));
            return v;
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return Optional.empty();
        });
    }


    /**
     * insert or update
     *
     * @param k KEY
     * @param v VALUE
     * @return true if inserted
     */
    public CompletableFuture<Boolean> put(K k, V v) {
        checkLoaded();
        CompletableFuture<Boolean> cf;
        if (cache.containsKey(k)) {
            cf = provider.update(k, v);
        } else {
            cf = provider.insert(k, v);
        }

        return cf.thenApply(b -> {
            if (b) {
                cache.put(k, v);
            }
            return b;
        }).exceptionally(throwable -> {
//            if (throwable instanceof SQLException) {
//                //todo
//            }
            throwable.printStackTrace();
            return false;
        });
    }

    /**
     * @param k KEY
     * @return true if deleted
     */

    public CompletableFuture<Boolean> remove(K k) {
        checkLoaded();
        if (cache.containsKey(k)) {
            return provider.remove(k).thenApply(b -> {
                if (b) {
                    cache.remove(k);
                }
                return b;
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                return false;
            });
        }
        return CompletableFuture.completedFuture(false);
    }


    public interface simpleDataProvider<K, V> {
        CompletableFuture<Optional<V>> get(@NotNull K key); // throw SQLException

        CompletableFuture<Optional<Map<K, V>>> getAll(); // throw SQLException

        CompletableFuture<@NotNull Boolean> insert(@NotNull K key, @NotNull V value); // throw SQLException

        CompletableFuture<@NotNull Boolean> update(@NotNull K key, @NotNull V value); // throw SQLException

        CompletableFuture<@NotNull Boolean> remove(@NotNull K key); // throw SQLException
    }

}
