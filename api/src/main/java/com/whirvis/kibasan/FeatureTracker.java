package com.whirvis.kibasan;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A feature tracker monitors the state of an input device and its features.
 * A common use for devices trackers is to fire callbacks (e.g., when a
 * device is connected, when a button is pressed, etc.)
 * <p/>
 * Unlike device adapters, a device tracker can be used for multiple input
 * devices (in fact, it's considered good practice.) Feature trackers will
 * typically use the same container type as the features they are tracking.
 * However, there is no requirement for this.
 *
 * @param <F> the feature type being tracked.
 * @param <S> the feature state container type.
 * @param <Z> the tracker state container type.
 */
public abstract class FeatureTracker<F extends DeviceFeature<S>, S, Z> {

    public final @NotNull Class<F> featureType;
    public final @NotNull Supplier<@NotNull Z> initialState;

    /**
     * The {@code featureType} is what determines which features this tracker
     * will monitor. When an input device registers a new feature, it checks
     * if the feature type is assignable from (i.e., extends) this type.
     * <p/>
     * Once an input device has determined this tracker monitors a feature,
     * {@link #checkFeature(InputDevice, DeviceFeature, Object, Object)} will
     * be called each time it is polled.
     *
     * @param featureType  the feature type class.
     * @param initialState a supplier for the initial tracking state.
     */
    public FeatureTracker(@NotNull Class<F> featureType,
                          @NotNull Supplier<@NotNull Z> initialState) {
        this.featureType = Objects.requireNonNull(featureType, "featureType");
        this.initialState = Objects.requireNonNull(initialState,
                "initialState");
    }

    /**
     * @param feature the feature to check.
     * @return {@code true} if this feature tracker monitors {@code feature},
     * {@code false} otherwise.
     */
    protected final boolean monitorsFeature(@NotNull DeviceFeature<?> feature) {
        return featureType.isAssignableFrom(feature.getClass());
    }

    /**
     * Called by {@code device} for each feature this tracker monitors when
     * updated via the {@link InputDevice#poll()} method.
     *
     * @param device       the device which has {@code feature}.
     * @param feature      the feature whose state to check.
     * @param featureState the current state of {@code feature}.
     * @param trackerState the state associated with {@code feature}
     *                     by this tracker.
     */
    protected abstract void checkFeature(@NotNull InputDevice device,
                                         @NotNull F feature,
                                         @NotNull S featureState,
                                         @NotNull Z trackerState);

}
