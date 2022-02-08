package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * A feature that's registered to an {@link IoDevice}. This container
 * exists to group together the information necessary to fetch and update
 * the state of a device feature.
 * <p>
 * For optimal performance, it is best to cache the state of a device feature
 * to a field for later retrieval. The state of a device feature can be fetched
 * via {@link IoDevice#getState(IoFeature)}.
 *
 * @param <F> the I/O feature type.
 * @param <S> the state container type.
 * @see FeatureState
 */
public class RegisteredFeature<F extends IoFeature<S>, S> {

    /**
     * This should be used when a feature has no updater. Its purpose to
     * increase speed by removing an unnecessary nullability check.
     */
    public static final Runnable NO_UPDATER = () -> {
        /* nothing to update */
    };

    public final @NotNull F feature;
    public final @NotNull S state;
    protected @NotNull Runnable updater;

    protected RegisteredFeature(@NotNull F feature) {
        this.feature = feature;
        this.state = feature.initialState.get();
        this.updater = NO_UPDATER;
    }

}
