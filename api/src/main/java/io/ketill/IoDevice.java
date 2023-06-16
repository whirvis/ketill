package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class IoDevice {

    private final String id;
    private final ReadWriteLock featuresLock;
    private final Map<String, IoFeature.Cache> features;

    public IoDevice(@NotNull String id) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.featuresLock = new ReentrantReadWriteLock();
        this.features = new HashMap<>();
    }

    /**
     * Returns the ID of this I/O device.
     * <p>
     * This should <i>not</i> be used for uniquely identifying different
     * {@code IoDevice} instances. That is, two instances of the same class
     * should return an identical ID.
     *
     * @return the ID of this I/O device.
     */
    public final @NotNull String getId() {
        return this.id;
    }

    private @Nullable IoFeature.Cache getCache(
            @NotNull IoFeature<?, ?> feature) {
        featuresLock.readLock().lock();
        try {
            IoFeature.Cache cache = features.get(feature.getId());
            if (cache == null || cache.feature != feature) {
                return null; /* same ID but different feature */
            }
            return cache;
        } finally {
            featuresLock.readLock().unlock();
        }
    }

    /**
     * Returns if an I/O feature is present on this device.
     *
     * @param feature the feature to check for.
     * @return {@code true} if {@code feature}, is present on this device,
     * {@code false} otherwise.
     * @see #hasFeature(String)
     */
    public final boolean hasFeature(@Nullable IoFeature<?, ?> feature) {
        if (feature == null) {
            return false;
        }
        return this.getCache(feature) != null;
    }

    /**
     * Returns if an I/O feature with the given ID is present on this device.
     *
     * @param id the feature ID, case-sensitive.
     * @return {@code true} if a feature with the given ID is present on this
     * device, {@code false} otherwise.
     * @see #hasFeature(IoFeature)
     */
    public final boolean hasFeature(@Nullable String id) {
        if (id == null) {
            return false;
        }
        featuresLock.readLock().lock();
        try {
            return features.containsKey(id);
        } finally {
            featuresLock.readLock().unlock();
        }
    }

    /**
     * Returns an I/O feature by its ID.
     *
     * @param id the feature ID, case-sensitive.
     * @return the feature with the given ID, {@code null} if no such
     * feature is present on this device.
     */
    public final @Nullable IoFeature<?, ?> getFeature(@Nullable String id) {
        if (id == null) {
            return null;
        }
        featuresLock.readLock().lock();
        try {
            IoFeature.Cache cache = features.get(id);
            return cache != null ? cache.feature : null;
        } finally {
            featuresLock.readLock().unlock();
        }
    }

    /**
     * Returns the number of I/O features present on this device.
     *
     * @return the number of I/O features present on this device.
     * @see #getFeatures()
     */
    public final int getFeatureCount() {
        featuresLock.readLock().lock();
        try {
            return features.size();
        } finally {
            featuresLock.readLock().unlock();
        }
    }

    /**
     * Returns all I/O features present on this device.
     * <p>
     * <b>Note:</b> The returned list is a copy.
     *
     * @return all I/O present features on this device.
     * @see #getFeatureCount()
     * @see #getStates()
     */
    public final @NotNull List<@NotNull IoFeature<?, ?>> getFeatures() {
        featuresLock.readLock().lock();
        try {
            List<IoFeature<?, ?>> copy = new ArrayList<>();
            for (IoFeature.Cache cache : features.values()) {
                copy.add(cache.feature);
            }
            return copy;
        } finally {
            featuresLock.readLock().unlock();
        }
    }

    /**
     * Returns the states of every I/O feature present on this device.
     * <p>
     * <b>Note:</b> The returned map is a copy.
     *
     * @return the states of every I/O feature present on this device.
     * @see #getFeatureCount()
     * @see #getFeatures()
     */
    public final @NotNull Map<@NotNull IoFeature<?, ?>, @NotNull IoState<?>> getStates() {
        featuresLock.readLock().lock();
        try {
            Map<IoFeature<?, ?>, IoState<?>> copy = new HashMap<>();
            for (IoFeature.Cache cache : features.values()) {
                copy.put(cache.feature, cache.state);
            }
            return copy;
        } finally {
            featuresLock.readLock().unlock();
        }
    }

    protected final <S extends IoState<I>, I, M extends S>
    @Nullable I getInternals(@NotNull IoFeature<S, M> feature) {
        featuresLock.readLock().lock();
        try {
            S state = this.getState(feature);
            return state != null ? state.internals : null;
        } finally {
            featuresLock.readLock().unlock();
        }
    }

    /**
     * Adds an I/O feature to the device.
     * <p>
     * If the given feature is already present, its current state will be
     * returned. A new instance will not be created in order to prevent a
     * dangling reference.
     * <p>
     * <b>Note:</b> No two features with the same ID can be present on a
     * device at the same time. This ensures {@link #getFeature(String)}
     * will always return a single and unambiguous value.
     *
     * @param feature the feature to add.
     * @param <S>     the I/O state type.
     * @param <I>     the internal data type.
     * @return the current state of {@code feature}.
     * @throws NullPointerException if {@code feature} is {@code null}.
     * @throws IoDeviceException    if a feature with the same ID as
     *                              {@code feature} is already present
     *                              on this device.
     */
    @SuppressWarnings("unchecked")
    protected <S extends IoState<I>, I, M extends S>
    @NotNull S addFeature(@NotNull IoFeature<S, M> feature) {
        Objects.requireNonNull(feature, "feature cannot be null");

        featuresLock.writeLock().lock();
        try {
            String id = feature.getId();

            /*
             * As explained by the docs, we must not create a new instance
             * of the state. By keeping the original object intact, previous
             * references will remain valid. Furthermore, there can be no
             * two features with the same ID present at one time.
             */
            IoFeature.Cache current = features.get(id);
            if (current != null && current.feature == feature) {
                return (S) current.state;
            } else if (current != null) {
                String msg = "feature with ID \"" + id + "\" already present";
                throw new IoDeviceException(this, msg);
            }

            S state = feature.createVerifiedState();
            M mutable = feature.createVerifiedMutableState(state);

            IoLogic<?> logic = feature.createVerifiedLogic(this, state);
            if (logic != null) {
                logic.startup();
            }

            features.put(id, new IoFeature.Cache(feature, state, mutable, logic));
            return state;
        } finally {
            featuresLock.writeLock().unlock();
        }
    }

    @SuppressWarnings("unchecked") /* we check it ourselves */
    protected final <S extends IoState<I>, I, M extends S>
    @Nullable S getState(@Nullable IoFeature<S, M> feature) {
        if (feature == null) {
            return null;
        }
        featuresLock.readLock().lock();
        try {
            IoFeature.Cache cache = features.get(feature.getId());
            if (cache == null || cache.feature != feature) {
                return null; /* unexpected feature in cache */
            }
            return (S) cache.state;
        } finally {
            featuresLock.readLock().unlock();
        }
    }

    @SuppressWarnings("unchecked") /* we check it ourselves */
    protected final <S extends IoState<I>, I, F extends IoFeature<S, M>, M extends S>
    @Nullable S getState(@Nullable String id, @NotNull Class<F> type) {
        if (id == null) {
            return null;
        }
        Objects.requireNonNull(type, "type cannot be null");
        featuresLock.readLock().lock();
        try {
            IoFeature.Cache cache = features.get(id);
            if (cache == null) {
                return null; /* unexpected feature in cache */
            }

            IoFeature<?, ?> feature = cache.feature;
            if (!type.isAssignableFrom(feature.getClass())) {
                throw new IoDeviceException(); /* TODO */
            }

            return (S) cache.state;
        } finally {
            featuresLock.readLock().unlock();
        }
    }

    protected final IoState<?> getState(@Nullable String id) {
        if (id == null) {
            return null;
        }
        featuresLock.readLock().lock();
        try {
            IoFeature.Cache cache = features.get(id);
            if (cache == null) {
                return null; /* unexpected feature in cache */
            }
            return cache.state;
        } finally {
            featuresLock.readLock().unlock();
        }
    }

    public void query() {

    }

    public void update() {

    }

}