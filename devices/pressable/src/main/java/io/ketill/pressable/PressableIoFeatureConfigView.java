package io.ketill.pressable;

/**
 * A read-only view of a {@link PressableIoFeatureConfig}.
 */
public interface PressableIoFeatureConfigView {

    /**
     * @return {@code true} if virtual feature pressing is enabled for
     * this device, {@code false} otherwise.
     */
    default boolean isHoldPressEnabled() {
        long interval = this.getHoldPressInterval();
        return interval > PressableIoFeatureConfig.DISABLE_HOLD_PRESS;
    }

    /**
     * @return how many milliseconds should pass between virtual feature
     * presses when a feature is being held down. A value less than one
     * indicates that this is disabled for the device.
     */
    long getHoldPressInterval();

    /**
     * @return {@code true} if feature holding is enabled for this device,
     * {@code false} otherwise.
     */
    default boolean isHoldEnabled() {
        long time = this.getHoldTime();
        return time > PressableIoFeatureConfig.DISABLE_HOLD;
    }

    /**
     * @return how long in milliseconds a feature must be pressed to be
     * considered held down. A negative value indicates that this is
     * disabled for the device.
     */
    long getHoldTime();

}
