package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * An {@link IoFeature} that's been registered to an {@link IoDevice}.
 * <p>
 * This container exists to group together the necessary information
 * to update the current state of an I/O feature.
 * <p>
 * <b>Thread safety:</b> This class is <i>thread-safe.</i>
 *
 * @param <F> the I/O feature type. Users can access this via the
 *            {@link #getFeature()} method when registering a feature.
 * @param <Z> the internal state type. The field containing the internal
 *            state is kept package-private. This prevents anyone other
 *            than the I/O device which owns it from accessing it.
 * @param <S> the state container type. Users can access this via the
 *            {@link #getState()} method when registering a feature.
 * @see FeaturePresent
 * @see FeatureState
 */
public final class RegisteredIoFeature<F extends IoFeature<Z, S>, Z, S> {

    /**
     * Used when a feature has no updater. Its purpose to increase
     * speed by removing an unnecessary nullability check.
     */
    public static final Runnable NO_UPDATER = () -> {
        /* nothing to update */
    };

    final @NotNull F feature;
    final @NotNull IoDeviceObserver observer;
    final @NotNull S containerState;
    final @NotNull Z internalState;
    final @NotNull Runnable autonomousUpdater;
    @NotNull Runnable adapterUpdater;

    RegisteredIoFeature(@NotNull F feature,
                        @NotNull IoDeviceObserver observer) {
        this.feature = feature;
        this.observer = observer;

        StatePair<Z, S> pair = feature.getState(observer);
        this.containerState = pair.container;
        this.internalState = pair.internal;

        if (internalState instanceof AutonomousState) {
            AutonomousState autonomy = (AutonomousState) internalState;
            this.autonomousUpdater = autonomy::update;
        } else {
            this.autonomousUpdater = NO_UPDATER;
        }

        this.adapterUpdater = NO_UPDATER;
    }

    /**
     * Returns the registered I/O feature.
     *
     * @return the registered I/O feature.
     */
    public @NotNull F getFeature() {
        return this.feature;
    }

    /**
     * Returns the current state of the feature.
     *
     * @return the current state of the feature.
     */
    public @NotNull S getState() {
        return this.containerState;
    }

    /* @formatter:off */
    @Override
    public String toString() {
        return ToStringUtils.getJoiner(this)
                .add("feature=" + feature)
                .toString();
    }
    /* @formatter:on */

}
