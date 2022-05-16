package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Emitted by {@link IoDevice} when a feature is registered.
 *
 * @see IoDevice#registerFeature(IoFeature)
 */
public class IoFeatureRegisterEvent extends IoDeviceEvent {

    private final @NotNull RegisteredFeature<?, ?, ?> registered;

    /**
     * @param device     the device which emitted this event.
     * @param registered the registration of the feature.
     * @throws NullPointerException if {@code device} or {@code feature}
     *                              are {@code null}.
     */
    IoFeatureRegisterEvent(@NotNull IoDevice device,
                           @NotNull RegisteredFeature<?, ?, ?> registered) {
        super(device);
        this.registered = Objects.requireNonNull(registered,
                "registered cannot be null");
    }

    /**
     * @return the registration of the feature.
     */
    public final @NotNull RegisteredFeature<?, ?, ?> getRegistration() {
        return this.registered;
    }

    /**
     * @return the feature which was registered.
     */
    public final @NotNull IoFeature<?, ?> getFeature() {
        return registered.feature;
    }

}
