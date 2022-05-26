package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A special feature registry which supports mapping I/O features.
 *
 * @see #mapFeature(IoFeature, Object, StateUpdater)
 */
public final class MappedFeatureRegistry implements FeatureRegistry {

    /* @formatter:off */
    private static final String DSE_MSG = "%s cannot be the %s" +
            " of a previously registered feature";
    /* @formatter:on */

    private final IoDeviceObserver observer;
    private final Map<IoFeature<?, ?>, RegisteredIoFeature<?, ?, ?>> features;
    private final Map<IoFeature<?, ?>, MappedFeature<?, ?, ?>> mappings;

    MappedFeatureRegistry(@NotNull IoDeviceObserver observer) {
        this.observer = observer;
        this.features = new HashMap<>();
        this.mappings = new HashMap<>();
    }

    /**
     * <b>Note:</b> A feature can be mapped without being registered. This
     * allows for an adapter to support a feature without being registered
     * to the device.
     *
     * @param feature the feature to check.
     * @return {@code true} if {@code feature} has an associated mapping,
     * {@code false} otherwise.
     * @throws NullPointerException if {@code feature} is {@code null}.
     */
    public boolean hasMapping(@NotNull IoFeature<?, ?> feature) {
        Objects.requireNonNull(feature, "feature cannot be null");
        return mappings.containsKey(feature);
    }

    /**
     * Maps a feature to a state updater. Each time {@link #updateFeatures()}
     * is called, every feature with a mapping will have their state updated
     * by their assigned state updater (assuming they have one.)
     * <p>
     * <b>Note:</b> A feature can be mapped without being registered. This
     * allows for an adapter to support a feature without being registered
     * to the device.
     *
     * @param feature the feature to map.
     * @param params  the params to map the feature by. Depending on the
     *                feature and adapter, this could be something like a
     *                button ID or a file path.
     * @param updater the method to call when updating the state.
     * @param <F>     the I/O feature type.
     * @param <Z>     the internal state type.
     * @param <P>     the mapping parameters type.
     * @throws NullPointerException if {@code feature} or {@code updater} are
     *                              {@code null}.
     * @see MappingMethod
     */
    /* @formatter:off */
    public <F extends IoFeature<Z, ?>, Z, P> void
            mapFeature(@NotNull F feature, @Nullable P params,
                       @NotNull StateUpdater<Z, P> updater) {
        Objects.requireNonNull(feature, "feature cannot be null");
        Objects.requireNonNull(updater, "updater cannot be null");
        mappings.put(feature, new MappedFeature<>(params, updater));
        this.updateMapping(feature);
    }
    /* @formatter:on */

    /**
     * Maps a feature to a state updater. Each time {@link #updateFeatures()}
     * is called, every feature with a mapping will have their state updated
     * by their assigned state updater (assuming they have one.)
     * <p>
     * <b>Note:</b> A feature can be mapped without being registered. This
     * allows for an adapter to support a feature without being registered
     * to the device.
     * <p>
     * <b>Shorthand for:</b>
     * {@link #mapFeature(IoFeature, Object, StateUpdater)}, with the
     * argument for {@code params} being {@code feature}.
     *
     * @param feature the feature to map.
     * @param updater the method to call when updating the state.
     * @param <F>     the I/O feature type.
     * @param <Z>     the internal state type.
     * @throws NullPointerException if {@code feature} or {@code updater} are
     *                              {@code null}.
     * @see MappingMethod
     */
    /* @formatter:off */
    public <F extends IoFeature<Z, ?>, Z> void
            mapFeature(@NotNull F feature,
                       @NotNull StateUpdater<Z, F> updater) {
        this.mapFeature(feature, feature, updater);
    }
    /* @formatter:on */

    /**
     * Maps a feature to a state updater. Each time {@link #updateFeatures()}
     * is called, every feature with a mapping will have their state updated
     * by their assigned state updater (assuming they have one.)
     * <p>
     * <b>Note:</b> A feature can be mapped without being registered. This
     * allows for an adapter to support a feature without being registered
     * to the device.
     * <p>
     * <b>Shorthand for:</b>
     * {@link #mapFeature(IoFeature, Object, StateUpdater)}, which passes
     * {@code params} as {@code null} and converts {@code updater} to an
     * instance of {@link StateUpdater}.
     *
     * @param feature the feature to map.
     * @param updater the method to call when updating the state.
     * @param <F>     the I/O feature type.
     * @param <Z>     the internal state type.
     * @throws NullPointerException if {@code feature} or {@code updater} are
     *                              {@code null}.
     * @see MappingMethod
     */
    /* @formatter:off */
    public <F extends IoFeature<Z, ?>, Z> void
            mapFeature(@NotNull F feature,
                       @NotNull StateUpdater.NoParams<Z> updater) {
        Objects.requireNonNull(updater, "updater cannot be null");
        this.mapFeature(feature, null,
                (state, params) -> updater.update(state));
    }
    /* @formatter:on */

    /**
     * Unmaps a feature from its current state updater. If the feature was
     * not previously mapped, this method does nothing.
     *
     * @param feature the feature to unmap.
     * @return {@code true} if {@code feature} was unmapped from its mapping,
     * {@code false} otherwise.
     * @throws NullPointerException if {@code feature} is {@code null}.
     */
    public boolean unmapFeature(@NotNull IoFeature<?, ?> feature) {
        Objects.requireNonNull(feature, "feature cannot be null");
        if (!mappings.containsKey(feature)) {
            return false;
        }
        Object removed = mappings.remove(feature);
        RegisteredIoFeature<?, ?, ?> registered = features.get(feature);
        if (registered != null) {
            registered.adapterUpdater = RegisteredIoFeature.NO_UPDATER;
        }
        return removed != null;
    }

    /* @formatter:off */
    @SuppressWarnings("unchecked")
    private <R extends RegisteredIoFeature<?, ?, ?>> void
            updateMapping(@NotNull IoFeature<?, ?> feature) {
        R registered = (R) features.get(feature);
        if (registered == null) {
            return;
        }

        MappedFeature<R, ?, ?> mapped =
                (MappedFeature<R, ?, ?>) mappings.get(feature);
        if (mapped != null) {
            registered.adapterUpdater = mapped.getUpdater(registered);
        } else {
            registered.adapterUpdater = RegisteredIoFeature.NO_UPDATER;
        }
    }
    /* @formatter:on */

    @Override
    public boolean isFeatureRegistered(@NotNull IoFeature<?, ?> feature) {
        Objects.requireNonNull(feature, "feature cannot be null");
        return features.containsKey(feature);
    }

    @Override
    public boolean isFeatureWithIdRegistered(@NotNull String id) {
        return this.getFeatureById(id) != null;
    }

    @Override
    public int getFeatureCount() {
        return features.size();
    }

    @Override
    public @Nullable IoFeature<?, ?> getFeatureById(@NotNull String id) {
        Objects.requireNonNull(id, "id cannot be null");
        for (IoFeature<?, ?> feature : features.keySet()) {
            if (id.equals(feature.getId())) {
                return feature;
            }
        }
        return null;
    }

    @Override
    public @NotNull Collection<@NotNull IoFeature<?, ?>> getFeatures() {
        return Collections.unmodifiableCollection(features.keySet());
    }

    /* @formatter:off */
    @Override
    @SuppressWarnings("unchecked")
    public <Z, S> @Nullable RegisteredIoFeature<?, Z, S>
            getFeatureRegistration(@NotNull IoFeature<Z, S> feature) {
        Objects.requireNonNull(feature, "feature cannot be null");
        return (RegisteredIoFeature<?, Z, S>) features.get(feature);
    }
    /* @formatter:on */

    /* @formatter:off */
    @Override
    public @NotNull Collection<@NotNull RegisteredIoFeature<?, ?, ?>>
            getFeatureRegistrations() {
        return Collections.unmodifiableCollection(features.values());
    }
    /* @formatter:on */

    /**
     * {@inheritDoc}
     * <p>
     * <b>Note:</b> I/O device adapters needing to access the internal state
     * of a feature can do so via {@link #getInternalState(IoFeature)}.
     */
    @Override
    public <S> @NotNull S getState(@NotNull IoFeature<?, S> feature) {
        return FeatureRegistry.super.getState(feature);
    }

    /**
     * This method exists for the benefit of device adapters. The internal
     * state of an I/O feature should <i>not</i> be publicly accessible.
     *
     * @param feature the feature whose state to fetch.
     * @param <Z>     the internal state type.
     * @return the internal state of {@code feature}.
     * @throws NullPointerException  if {@code feature} is {@code null}.
     * @throws IllegalStateException if {@code feature} is not registered.
     * @see #getState(IoFeature)
     */
    public <Z> Z getInternalState(@NotNull IoFeature<Z, ?> feature) {
        Objects.requireNonNull(feature, "feature cannot be null");
        RegisteredIoFeature<?, Z, ?> registered =
                this.getFeatureRegistration(feature);
        if (registered == null) {
            String msg = "no such feature \"" + feature.getId() + "\"";
            throw new IllegalStateException(msg);
        }
        return registered.internalState;
    }

    /* @formatter:off */
    @Override
    public <F extends IoFeature<Z, S>, Z, S>
            @NotNull RegisteredIoFeature<F, Z, S>
            registerFeature(@NotNull F feature) {
        Objects.requireNonNull(feature, "feature cannot be null");
        if (this.isFeatureRegistered(feature)) {
            throw new IllegalStateException("feature already registered");
        }

        /*
         * No two features with the same ID can be registered at the same
         * time. This is to prevent confusion when debugging, and allows
         * for things like configurations relating to a feature to safely
         * use their ID as the key.
         */
        if(this.isFeatureWithIdRegistered(feature.getId())) {
            String msg = "feature with ID \"" + feature.getId() + "\"";
            msg += " already registered";
            throw new IllegalStateException(msg);
        }

        RegisteredIoFeature<F, Z, S> registered =
                new RegisteredIoFeature<>(feature, observer);

        /*
         * The feature being registered must not have any states that are
         * the same as the state of a previously registered feature. This
         * would cause confusion and break methods.
         */
        for(RegisteredIoFeature<?, ?, ?> rf : features.values()) {
            if(registered.internalState == rf.internalState) {
                throw new IllegalStateException(String.format(DSE_MSG,
                        "internalState", "internalState"));
            } else if(registered.internalState == rf.containerState) {
                throw new IllegalStateException(String.format(DSE_MSG,
                        "internalState", "containerState"));
            } else if(registered.containerState == rf.internalState) {
                throw new IllegalStateException(String.format(DSE_MSG,
                        "containerState", "internalState"));
            } else if(registered.containerState == rf.containerState) {
                throw new IllegalStateException(String.format(DSE_MSG,
                        "containerState", "containerState"));
            }
        }

        features.put(feature, registered);
        this.updateMapping(feature);

        return registered;
    }
    /* @formatter:on */

    @Override
    public void unregisterFeature(@NotNull IoFeature<?, ?> feature) {
        Objects.requireNonNull(feature, "feature cannot be null");
        if (!this.isFeatureRegistered(feature)) {
            throw new IllegalStateException("feature not registered");
        }
        features.remove(feature);
    }

    void updateFeatures() {
        for (RegisteredIoFeature<?, ?, ?> registered : features.values()) {
            registered.adapterUpdater.run();
            registered.autonomousUpdater.run();
        }
    }

}
