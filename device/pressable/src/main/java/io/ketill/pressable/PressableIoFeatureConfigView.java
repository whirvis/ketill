package io.ketill.pressable;

/**
 * A read-only view of a {@link PressableIoFeatureConfig}.
 */
public interface PressableIoFeatureConfigView {

    /**
     * Returns if virtual feature pressing is enabled.
     *
     * @return {@code true} if virtual feature pressing is enabled,
     * {@code false} otherwise.
     */
    default boolean isHoldPressEnabled() {
        long interval = this.getHoldPressInterval();
        return interval > PressableIoFeatureConfig.DISABLE_HOLD_PRESS;
    }

    /**
     * Returns how many milliseconds should pass between virtual feature
     * presses when a feature is being held down.
     *
     * @return how many milliseconds should pass between virtual feature
     * presses when a feature is being held down. A value less than one
     * indicates that this is disabled.
     */
    long getHoldPressInterval();

    /**
     * Returns if feature holding is enabled.
     *
     * @return {@code true} if feature holding is enabled, {@code false}
     * otherwise.
     */
    default boolean isHoldEnabled() {
        long time = this.getHoldTime();
        return time > PressableIoFeatureConfig.DISABLE_HOLD;
    }

    /**
     * Returns how long in milliseconds a feature must be pressed to
     * be considered held down.
     *
     * @return how long in milliseconds a feature must be pressed to
     * be considered held down. A negative value indicates that this
     * is disabled.
     */
    long getHoldTime();

}
