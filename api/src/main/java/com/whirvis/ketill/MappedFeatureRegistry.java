package com.whirvis.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A special feature registry which supports mapping device features.
 *
 * @see #mapFeature(DeviceFeature, Object, StateUpdater)
 */
public class MappedFeatureRegistry implements FeatureRegistry {

    private final Map<DeviceFeature<?>, RegisteredFeature<?, ?>> features;
    private final Map<DeviceFeature<?>, MappedFeature<?, ?, ?>> mappings;

    protected MappedFeatureRegistry() {
        this.features = new HashMap<>();
        this.mappings = new HashMap<>();
    }

    /**
     * @param feature the feature to check.
     * @return {@code true} if {@code feature} has an associated mapping,
     * {@code false} otherwise.
     * @throws NullPointerException if {@code feature} is {@code null}.
     */
    public boolean hasMapping(@NotNull DeviceFeature<?> feature) {
        Objects.requireNonNull(feature, "feature");
        return mappings.containsKey(feature);
    }

    /**
     * Maps a feature to a state updater. Each time {@link #updateFeatures()}
     * is called, every feature with a mapping will have their state updated
     * by their assigned state updater (assuming they have one.)
     *
     * @param feature the feature to map.
     * @param params  the params to map the feature by. Depending on the
     *                feature and adapter, this could be something like a
     *                button ID or a file path.
     * @param updater the method to call when updating the state.
     * @param <F>     the device feature type.
     * @param <S>     the state container type.
     * @param <P>     the mapping parameters type.@throws
     * @throws NullPointerException if {@code feature} or {@code updater} are
     *                              {@code null}.
     */
    /* @formatter:off */
    public <F extends DeviceFeature<S>, S, P> void
            mapFeature(@NotNull F feature, @Nullable P params,
                       @NotNull StateUpdater<S, P> updater) {
        Objects.requireNonNull(feature, "feature");
        Objects.requireNonNull(updater, "updater");
        mappings.put(feature, new MappedFeature<>(params, updater));
        this.updateMapping(feature);
    }
    /* @formatter:on */

    /**
     * Maps a feature to a state updater. Each time {@link #updateFeatures()}
     * is called, every feature with a mapping will have their state updated
     * by their assigned state updater (assuming they have one.)
     * <p/>
     * This method is a shorthand for
     * {@link #mapFeature(DeviceFeature, Object, StateUpdater)}, with the
     * argument for {@code params} being {@code feature}.
     *
     * @param feature the feature to map.
     * @param updater the method to call when updating the state.
     * @param <F>     the device feature type.
     * @param <S>     the state container type.
     * @throws NullPointerException if {@code feature} or {@code updater} are
     *                              {@code null}.
     */
    /* @formatter:off */
    public final <F extends DeviceFeature<S>, S> void
            mapFeature(@NotNull F feature,
                       @NotNull StateUpdater<S, F> updater) {
        this.mapFeature(feature, feature, updater);
    }
    /* @formatter:on */

    /**
     * Maps a feature to a state updater. Each time {@link #updateFeatures()}
     * is called, every feature with a mapping will have their state updated
     * by their assigned state updater (assuming they have one.)
     * <p/>
     * This method is a shorthand for
     * {@link #mapFeature(DeviceFeature, Object, StateUpdater)}, which
     * passes {@code params} as {@code null} and converts {@code updater}
     * to an instance of {@link StateUpdater}.
     *
     * @param feature the feature to map.
     * @param updater the method to call when updating the state.
     * @param <F>     the device feature type.
     * @param <S>     the state container type.
     * @throws NullPointerException if {@code feature} or {@code updater} are
     *                              {@code null}.
     */
    /* @formatter:off */
    public final <F extends DeviceFeature<S>, S> void
            mapFeature(@NotNull F feature,
                       @NotNull StateUpdater.NoParams<S> updater) {
        Objects.requireNonNull(updater, "updater");
        this.mapFeature(feature, null,
                (state, params) -> updater.update(state));
    }
    /* @formatter:on */

    /**
     * @param feature the feature to unmap.
     * @return {@code true} if {@code feature} was unmapped from its mapping,
     * {@code false} otherwise.
     * @throws NullPointerException if {@code feature} is {@code null}.
     */
    public boolean unmapFeature(@NotNull DeviceFeature<?> feature) {
        Objects.requireNonNull(feature, "feature");
        if (!mappings.containsKey(feature)) {
            return false;
        }
        Object removed = mappings.remove(feature);
        RegisteredFeature<?, ?> registered = features.get(feature);
        if (registered != null) {
            registered.updater = RegisteredFeature.NO_UPDATER;
        }
        return removed != null;
    }

    /* @formatter:off */
    @SuppressWarnings("unchecked")
    private <R extends RegisteredFeature<?, ?>> void
            updateMapping(@NotNull DeviceFeature<?> feature) {
        R registered = (R) features.get(feature);
        if (registered == null) {
            return;
        }

        MappedFeature<R, ?, ?> mapped =
                (MappedFeature<R, ?, ?>) mappings.get(feature);
        if (mapped != null) {
            registered.updater = mapped.getUpdater(registered);
        } else {
            registered.updater = RegisteredFeature.NO_UPDATER;
        }
    }
    /* @formatter:on */

    @Override
    public boolean isRegistered(@NotNull DeviceFeature<?> feature) {
        Objects.requireNonNull(feature, "feature");
        return features.containsKey(feature);
    }

    /* @formatter:off */
    @Override
    public @NotNull Collection<@NotNull RegisteredFeature<?, ?>>
            getFeatures() {
        return Collections.unmodifiableCollection(features.values());
    }
    /* @formatter:on */

    /* @formatter:off */
    @Override
    @SuppressWarnings("unchecked")
    public <S> @Nullable RegisteredFeature<?, S>
            getRegistered(@NotNull DeviceFeature<S> feature) {
        Objects.requireNonNull(feature, "feature");
        return (RegisteredFeature<?, S>) features.get(feature);
    }
    /* @formatter:on */

    /* @formatter:off */
    @Override
    public <F extends DeviceFeature<S>, S> @NotNull RegisteredFeature<F, S>
            registerFeature(@NotNull F feature) {
        Objects.requireNonNull(feature, "feature");
        if (this.isRegistered(feature)) {
            throw new IllegalStateException("feature already registered");
        }

        RegisteredFeature<F, S> registered = new RegisteredFeature<>(feature);
        features.put(feature, registered);
        this.updateMapping(feature);

        return registered;
    }
    /* @formatter:on */

    @Override
    public void unregisterFeature(@NotNull DeviceFeature<?> feature) {
        Objects.requireNonNull(feature, "feature");
        if (!this.isRegistered(feature)) {
            throw new IllegalStateException("feature not registered");
        }
        features.remove(feature);
    }

    protected void updateFeatures() {
        for (RegisteredFeature<?, ?> registered : features.values()) {
            registered.updater.run();
        }
    }

}