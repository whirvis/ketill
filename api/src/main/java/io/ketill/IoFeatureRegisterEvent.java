package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDevice} when a feature is registered.
 *
 * @see IoDevice#registerFeature(IoFeature)
 */
public final class IoFeatureRegisterEvent extends IoFeatureEvent {

    private final @NotNull RegisteredIoFeature<?, ?, ?> registered;

    IoFeatureRegisterEvent(@NotNull IoDevice device,
                           @NotNull RegisteredIoFeature<?, ?, ?> registered) {
        super(device, registered.feature);
        this.registered = registered;
    }

    /**
     * Returns the feature registration.
     *
     * @return the feature registration.
     */
    public @NotNull RegisteredIoFeature<?, ?, ?> getRegistration() {
        return this.registered;
    }

}
