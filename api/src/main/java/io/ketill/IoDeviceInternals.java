package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@IoApi.Friends(IoDevice.class)
final class IoDeviceInternals {

    private final String typeId;

    private final ReadWriteLock featuresLock;
    private final Map<String, IoFeature.Cache> features;

    private final List<IoFeature<?, ?>> featureList;
    private final Map<IoFeature<?, ?>, IoState<?>> statesMap;
    private final Map<IoFeature<?, ?>, IoState<?>> mutableMap;

    public IoDeviceInternals(@NotNull String typeId) {
        this.typeId = IoApi.validateId(typeId);
        this.featuresLock = new ReentrantReadWriteLock();

        this.features = new HashMap<>();
        this.featureList = new ArrayList<>();
        this.statesMap = new HashMap<>();
        this.mutableMap = new HashMap<>();
    }

    public String getTypeId() {
        return this.typeId;
    }

    public @Nullable IoFeature.Cache
    getFeatureCache(@Nullable IoFeature<?, ?> feature) {
        if (feature == null) {
            return null;
        }
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

    public @Nullable IoFeature.Cache
    getFeatureCache(@Nullable String id) {
        if (id == null) {
            return null;
        }
        featuresLock.readLock().lock();
        try {
            return features.get(id);
        } finally {
            featuresLock.readLock().unlock();
        }
    }

    int getFeatureCount() {
        featuresLock.readLock().lock();
        try {
            return features.size();
        } finally {
            featuresLock.readLock().unlock();
        }
    }

    public @NotNull List<@NotNull IoFeature<?, ?>> getFeatures(
            boolean snapshot) {
        if (!snapshot) {
            return Collections.unmodifiableList(featureList);
        }
        featuresLock.readLock().lock();
        try {
            return new ArrayList<>(featureList);
        } finally {
            featuresLock.readLock().unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public <I, S extends IoState<I>, M extends S>
    @NotNull S addFeature(@NotNull IoDevice device,
                          @NotNull IoFeature<S, M> feature) {
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
                throw new IoDeviceException(device, msg);
            }

            S state = feature.createVerifiedState();
            M mutable = feature.createVerifiedMutableState(state);

            IoLogic<?> logic = feature.createVerifiedLogic(device, state);
            if (logic != null) {
                logic.startup();
            }

            features.put(id, new IoFeature.Cache(feature, state, mutable, logic));

            featureList.add(feature);
            statesMap.put(feature, state);
            mutableMap.put(feature, mutable);

            return state;
        } finally {
            featuresLock.writeLock().unlock();
        }
    }

    public @NotNull
    Map<@NotNull IoFeature<?, ?>, @NotNull IoState<?>>
    getStates(boolean snapshot) {
        if (!snapshot) {
            return Collections.unmodifiableMap(statesMap);
        }
        featuresLock.readLock().lock();
        try {
            return new HashMap<>(statesMap);
        } finally {
            featuresLock.readLock().unlock();
        }
    }

    public @Nullable IoFeature.Cache
    getFeatureCache(@Nullable String id,
                    @NotNull Class<? extends IoFeature<?, ?>> type) {
        Objects.requireNonNull(type, "type cannot be null");
        IoFeature.Cache cache = this.getFeatureCache(id);
        if (cache == null) {
            return null;
        }
        IoFeature<?, ?> feature = cache.feature;
        if (!type.isAssignableFrom(feature.getClass())) {
            return null; /* unexpected type */
        }
        return cache;
    }

}