package com.whirvis.kibasan;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A tracker that's been added to an {@link InputDevice}. This container
 * exists to group together the information necessary to notify a feature
 * tracker of changes to the state of a device feature.
 *
 * @param <F> the device feature type.
 * @param <S> the feature state container type.
 * @param <Z> the tracker state container type.
 * @see FeatureTracker
 */
public class RegisteredTracker<F extends DeviceFeature<S>, S, Z> {

    public final @NotNull InputDevice device;
    public final @NotNull FeatureTracker<F, S, Z> tracker;
    private final @NotNull Map<RegisteredFeature<F, S>, Z> features;

    protected RegisteredTracker(@NotNull InputDevice device,
                                @NotNull FeatureTracker<F, S, Z> tracker) {
        this.device = device;
        this.tracker = tracker;
        this.features = new HashMap<>();
    }

    /* it actually is checked */
    @SuppressWarnings("unchecked")
    protected void trackFeature(@NotNull RegisteredFeature<?, ?> feature) {
        if (tracker.monitorsFeature(feature.feature)) {
            RegisteredFeature<F, S> castedFeature =
                    (RegisteredFeature<F, S>) feature;
            features.put(castedFeature, tracker.initialState.get());
        }
    }

    protected void dropFeature(@NotNull RegisteredFeature<?, ?> feature) {
        features.remove(feature);
    }

    protected void checkFeatures() {
        for (Map.Entry<RegisteredFeature<F, S>, Z> e : features.entrySet()) {
            RegisteredFeature<F, S> registeredFeature = e.getKey();
            tracker.checkFeature(device, registeredFeature.feature,
                    registeredFeature.state, e.getValue());
        }
    }

}
