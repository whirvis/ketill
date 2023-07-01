package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Smart container for I/O device features.
 * <p>
 * This class exists to make the implementation of {@link IoDevice} cleaner.
 * The code for managing I/O features is straight forward but can look messy
 * with all the generics involved.
 * <p>
 * <b>Visibility:</b> This class is {@code package-private} as it is
 * not meant for use outside implementation of the API.
 *
 * @see #add(IoFeature)
 */
@IoApi.Friends(IoDevice.class)
final class IoDeviceFeatures implements Iterable<IoState<?>> {

    private final @NotNull IoDevice device;

    private final @NotNull ReadWriteLock containerLock;
    private final @NotNull Map<String, IoFeature.Cache> cache;
    private final @NotNull List<IoFeature<?>> features;
    private final @NotNull Map<IoFeature<?>, IoState<?>> states;

    /**
     * Constructs a new {@code IoDeviceFeatures}.
     *
     * @param device the I/O device this container belongs to.
     * @throws NullPointerException if {@code device} is {@code null}.
     */
    IoDeviceFeatures(@NotNull IoDevice device) {
        this.device = Objects.requireNonNull(device,
                "device cannot be null");
        this.containerLock = new ReentrantReadWriteLock();
        this.cache = new HashMap<>();
        this.features = new ArrayList<>();
        this.states = new HashMap<>();
    }

    /**
     * Returns the number of I/O features.
     *
     * @return the number of I/O features.
     */
    int size() {
        containerLock.readLock().lock();
        try {
            return cache.size();
        } finally {
            containerLock.readLock().unlock();
        }
    }

    /**
     * Returns the cache for an I/O feature.
     *
     * @param feature the feature to query.
     * @return the cache for the requested feature or {@code null} if it has
     * not been added.
     * @see #getCache(String)
     */
    @Nullable IoFeature.Cache getCache(@Nullable IoFeature<?> feature) {
        if (feature == null) {
            return null;
        }
        containerLock.readLock().lock();
        try {
            IoFeature.Cache cache = this.cache.get(feature.getId());
            if (cache == null || cache.feature != feature) {
                return null; /* same ID but different feature */
            }
            return cache;
        } finally {
            containerLock.readLock().unlock();
        }
    }

    /**
     * Returns the cache for an I/O feature by its ID.
     *
     * @param id the feature ID, case-sensitive.
     * @return the cache for the feature with the given ID or {@code null}
     * if no such feature has been added.
     * @see #getCache(IoFeature)
     */
    @Nullable IoFeature.Cache getCache(@Nullable String id) {
        if (id == null) {
            return null;
        }
        containerLock.readLock().lock();
        try {
            return cache.get(id);
        } finally {
            containerLock.readLock().unlock();
        }
    }

    /**
     * Returns a list of all I/O features in this container.
     * <p>
     * <b>Note:</b> The returned list is an <i>unmodifiable</i> direct view.
     *
     * @return a list of all I/O features in this container.
     * @see #asMap()
     */
    @NotNull List<@NotNull IoFeature<?>> asList() {
        return Collections.unmodifiableList(features);
    }

    /**
     * Returns a map of all I/O states in this container. The key for each
     * entry is the I/O feature, and the value is its state.
     * <p>
     * <b>Note:</b> The returned map is an <i>unmodifiable</i> direct view.
     *
     * @return a map of all I/O states in this container.
     * @see #asList()
     */
    @NotNull Map<@NotNull IoFeature<?>, @NotNull IoState<?>> asMap() {
        return Collections.unmodifiableMap(states);
    }

    /**
     * Adds an I/O feature to this container.
     * <p>
     * If the given feature is already present, its current state will be
     * returned. A new instance will not be created in order to prevent a
     * dangling reference. This ensures the state of a feature will remain
     * valid for the lifetime of this container.
     * <p>
     * <b>Note:</b> No two features with the same ID can be present within
     * this container at the same time. This ensures {@link #getCache(String)}
     * will always return a single and unambiguous value.
     *
     * @param feature the feature to add.
     * @param <S>     the I/O state type.
     * @param <I>     the internal data type.
     * @return the current state of {@code feature}, which is guaranteed to
     * remain valid for the lifetime of this container.
     * @throws NullPointerException if {@code feature} is {@code null}.
     * @throws IoDeviceException    if a feature with the same ID as
     *                              {@code feature} is already present
     *                              within this container.
     * @throws IoFeatureException   if the device this container belongs to
     *                              is not of the feature's required type.
     */
    @SuppressWarnings("unchecked")
    <I, S extends IoState<I>> @NotNull S
    add(@NotNull IoFeature<S> feature) {
        Objects.requireNonNull(feature, "feature cannot be null");

        containerLock.writeLock().lock();
        try {
            String id = feature.getId();

            /*
             * As explained by the docs, we must not create a new instance
             * of the state. By keeping the original object intact, previous
             * references will remain valid. Furthermore, there can be no
             * two features with the same ID present at one time.
             */
            IoFeature.Cache current = cache.get(id);
            if (current != null && current.feature == feature) {
                return (S) current.state;
            } else if (current != null) {
                String msg = "feature with ID \"" + id + "\" already present";
                throw new IoDeviceException(device, msg);
            }

            S state = feature.createVerifiedState(device);

            cache.put(id, new IoFeature.Cache(feature, state));
            features.add(feature);
            states.put(feature, state);

            return state;
        } finally {
            containerLock.writeLock().unlock();
        }
    }

    @Override
    public @NotNull Iterator<@NotNull IoState<?>> iterator() {
        return states.values().iterator();
    }

}