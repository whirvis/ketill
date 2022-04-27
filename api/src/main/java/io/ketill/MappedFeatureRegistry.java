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

    private final Map<IoFeature<?, ?>, RegisteredFeature<?, ?, ?>> features;
    private final Map<IoFeature<?, ?>, MappedFeature<?, ?, ?>> mappings;

    MappedFeatureRegistry() {
        this.features = new HashMap<>();
        this.mappings = new HashMap<>();
    }

    /**
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
     * This method is a shorthand for
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
     * This method is a shorthand for
     * {@link #mapFeature(IoFeature, Object, StateUpdater)}, which
     * passes {@code params} as {@code null} and converts {@code updater}
     * to an instance of {@link StateUpdater}.
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
        RegisteredFeature<?, ?, ?> registered = features.get(feature);
        if (registered != null) {
            registered.updater = RegisteredFeature.NO_UPDATER;
        }
        return removed != null;
    }

    /* @formatter:off */
    @SuppressWarnings("unchecked")
    private <R extends RegisteredFeature<?, ?, ?>> void
            updateMapping(@NotNull IoFeature<?, ?> feature) {
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
    public boolean isFeatureRegistered(@NotNull IoFeature<?, ?> feature) {
        Objects.requireNonNull(feature, "feature cannot be null");
        return features.containsKey(feature);
    }

    @Override
    public int getFeatureCount() {
        return features.size();
    }

    /* @formatter:off */
    @Override
    public @NotNull Collection<@NotNull RegisteredFeature<?, ?, ?>>
            getFeatures() {
        return Collections.unmodifiableCollection(features.values());
    }
    /* @formatter:on */

    /* @formatter:off */
    @Override
    @SuppressWarnings("unchecked")
    public <Z, S> @Nullable RegisteredFeature<?, Z, S>
            getFeatureRegistration(@NotNull IoFeature<Z, S> feature) {
        Objects.requireNonNull(feature, "feature cannot be null");
        return (RegisteredFeature<?, Z, S>) features.get(feature);
    }
    /* @formatter:on */

    /* @formatter:off */
    @Override
    public <F extends IoFeature<Z, S>, Z, S>
            @NotNull RegisteredFeature<F, Z, S>
            registerFeature(@NotNull F feature) {
        Objects.requireNonNull(feature, "feature cannot be null");
        if (this.isFeatureRegistered(feature)) {
            throw new IllegalStateException("feature already registered");
        }

        RegisteredFeature<F, Z, S> registered =
                new RegisteredFeature<>(feature);

        /*
         * The feature being registered must not have any states that are
         * the same as the state of a previously registered feature. This
         * would cause confusion and break methods.
         */
        for(RegisteredFeature<?, ?, ?> rf : features.values()) {
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
        for (RegisteredFeature<?, ?, ?> registered : features.values()) {
            registered.updater.run();
        }
    }

}
