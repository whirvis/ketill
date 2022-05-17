package io.ketill.pressable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Used by {@link PressableIoFeatureObserver} to decide if a pressable feature
 * is being held down, how quickly it should be virtually pressed, etc.
 */
public class PressableIoFeatureConfig implements PressableIoFeatureConfigView {

    /* @formatter:off */
    /**
     * @param view the configuration view to filter.
     * @return {@code view} as given if not {@code null},
     * {@link PressableIoFeatureConfig#DEFAULT} otherwise.
     */
    public static @NotNull PressableIoFeatureConfigView
            valueOf(@Nullable PressableIoFeatureConfigView view) {
        return view != null ? view : PressableIoFeatureConfig.DEFAULT;
    }
    /* @formatter:on */

    /* @formatter:off */
    public static final long
            DEFAULT_HOLD_TIME = 1000L,
            DEFAULT_HOLD_PRESS_INTERVAL = 100L,
            DISABLE_HOLD = -1L,
            DISABLE_HOLD_PRESS = 0L;

    public static final @NotNull PressableIoFeatureConfigView
            DEFAULT = new PressableIoFeatureConfig();
    /* @formatter:on */

    private long holdTime;
    private long holdPressInterval;

    /**
     * @param holdTime          how long in milliseconds a feature must
     *                          be pressed to be considered held down.
     *                          A negative value will disable holding
     *                          states for this device.
     * @param holdPressInterval how many milliseconds should pass between
     *                          virtual feature presses. Values less than
     *                          one will disable this feature.
     */
    public PressableIoFeatureConfig(long holdTime, long holdPressInterval) {
        this.setHoldTime(holdTime);
        this.setHoldPressInterval(holdPressInterval);
    }

    /**
     * Constructs a new {@code HoldableFeatureSettings} with the argument
     * for {@code holdTime} being {@value #DEFAULT_HOLD_TIME} and
     * {@code holdPressInterval} being {@value #DEFAULT_HOLD_PRESS_INTERVAL}.
     */
    public PressableIoFeatureConfig() {
        this(DEFAULT_HOLD_TIME, DEFAULT_HOLD_PRESS_INTERVAL);
    }

    @Override
    public long getHoldTime() {
        return this.holdTime;
    }

    /**
     * Sets how long in milliseconds a feature must be pressed for it to
     * be considered held down. Once a feature is held down, the feature
     * press callback (if set) will be automatically fired. The virtual
     * press rate is determined by {@link #getHoldPressInterval()}.
     *
     * @param holdTime how long in milliseconds a feature must be pressed
     *                 to be considered held down. A negative value will
     *                 disable holding states for this device.
     * @see #setHoldPressInterval(long)
     */
    public void setHoldTime(long holdTime) {
        this.holdTime = holdTime;
    }

    @Override
    public long getHoldPressInterval() {
        return this.holdPressInterval;
    }

    /**
     * Sets how many milliseconds should pass between virtual feature
     * presses when a feature is considered to be held down. The time
     * required for a feature to be considered held down is determined
     * by {@link #getHoldTime()}.
     *
     * @param holdPressInterval how many milliseconds should pass between
     *                          virtual feature presses. Values less than
     *                          one will disable this feature.
     * @see #setHoldTime(long)
     */
    public void setHoldPressInterval(long holdPressInterval) {
        this.holdPressInterval = holdPressInterval;
    }

}
