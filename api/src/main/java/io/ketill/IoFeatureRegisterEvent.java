package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDevice} when an {@link IoFeature} is registered.
 *
 * @see IoDevice#registerFeature(IoFeature)
 */
public final class IoFeatureRegisterEvent extends IoFeatureEvent {

    private final @NotNull RegisteredIoFeature<?, ?, ?> registered;

    IoFeatureRegisterEvent(@NotNull IoDevice emitter,
                           @NotNull RegisteredIoFeature<?, ?, ?> registered) {
        super(emitter, registered.feature);
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
