package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDevice} when a feature is registered.
 *
 * @see IoDevice#registerFeature(IoFeature)
 */
public final class IoFeatureRegisterEvent extends IoFeatureEvent {

    private final @NotNull RegisteredIoFeature<?, ?, ?> registered;

    /**
     * @param device     the device which emitted this event.
     * @param registered the registration of the feature.
     * @throws NullPointerException if {@code device} or {@code feature}
     *                              are {@code null}.
     */
    IoFeatureRegisterEvent(@NotNull IoDevice device,
                           @NotNull RegisteredIoFeature<?, ?, ?> registered) {
        super(device, registered.feature);
        this.registered = registered;
    }

    /**
     * @return the registration of the feature.
     */
    public @NotNull RegisteredIoFeature<?, ?, ?> getRegistration() {
        return this.registered;
    }

}
