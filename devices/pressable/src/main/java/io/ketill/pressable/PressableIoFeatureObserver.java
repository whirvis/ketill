package io.ketill.pressable;

import io.ketill.AutonomousState;
import io.ketill.IoDevice;
import io.ketill.IoDeviceObserver;
import io.ketill.IoFeature;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * An object which observes the state of an I/O feature, specifically one
 * which can be considered to be pressed or released (this is determined
 * via the {@link #isPressed()} method). The associated event is emitted
 * when the state of the observed feature changes.
 * <p>
 * <b>Note:</b> For events to be emitted, the monitor must be polled
 * periodically via the {@link #poll()} method. This can be achieved
 * by implementing {@link AutonomousState} in the internal state of
 * an I/O feature and calling it from there.
 *
 * @see PressableIoFeatureEvent
 * @see PressableIoFeatureConfig
 * @see PressableIoFeatureSupport
 */
public abstract class PressableIoFeatureObserver<Z> {

    protected final @NotNull IoFeature<Z, ?> feature;
    protected final @NotNull Z internalState;
    protected final @NotNull IoDeviceObserver observer;
    protected final @NotNull IoDevice device;
    protected final @NotNull Supplier<PressableIoFeatureConfigView> configSupplier;

    private boolean pressed;
    private boolean held;
    private long lastPressTime;

    /**
     * @param feature       the feature whose state is being observed.
     * @param internalState the internal state of {@code feature}.
     * @param observer      the I/O device observer.
     * @throws NullPointerException     if {@code feature},
     *                                  {@code internalState}, or
     *                                  {@code observer} are {@code null}.
     * @throws IllegalStateException    if {@code feature} is not registered
     *                                  to the device of {@code observer}.
     * @throws IllegalArgumentException if {@code internalState} does not
     *                                  belong to {@code feature}.
     */
    public PressableIoFeatureObserver(@NotNull IoFeature<Z, ?> feature,
                                      @NotNull Z internalState,
                                      @NotNull IoDeviceObserver observer) {
        this.feature = Objects.requireNonNull(feature,
                "feature cannot be null");
        this.internalState = Objects.requireNonNull(internalState,
                "internalState cannot be null");
        this.observer = Objects.requireNonNull(observer,
                "observer cannot be null");
        this.device = observer.getDevice();

        if (!device.isFeatureRegistered(feature)) {
            String msg = "feature must be registered to device";
            throw new IllegalStateException(msg);
        } else if (device.getFeature(internalState) != feature) {
            String msg = "internalState must belong to feature";
            throw new IllegalArgumentException(msg);
        }

        if (device instanceof PressableIoFeatureSupport) {
            PressableIoFeatureSupport support =
                    (PressableIoFeatureSupport) device;
            this.configSupplier = support::getPressableConfig;
        } else {
            this.configSupplier = () -> PressableIoFeatureConfig.DEFAULT;
        }
    }

    /**
     * This is used to determine when emits should be emitted. The behavior
     * of event emission can be changed by overriding {@link #onPress()},
     * {@link #onHold()}, and/or {@link #onRelease()}.
     *
     * @return {@code true} if the state of the feature indicates that it is
     * currently pressed down, {@code false} otherwise.
     */
    protected abstract boolean isPressed();

    /**
     * Called when the feature is first pressed down, or virtually
     * pressed due to being held down. By default, this method emits
     * an {@link IoFeaturePressEvent}.
     * <p>
     * This method can be overridden to change its behavior. For example,
     * by emitting a different type of event or by updating the observed
     * state.
     */
    protected void onPress() {
        observer.onNext(new IoFeaturePressEvent(device, feature));
    }

    /**
     * Called when the feature is first held down, due to being pressed
     * for an extended period of time. By default, this method emits an
     * {@link IoFeatureHoldEvent}.
     * <p>
     * This method can be overridden to change its behavior. For example,
     * by emitting a different type of event or by updating the observed
     * state.
     */
    protected void onHold() {
        observer.onNext(new IoFeatureHoldEvent(device, feature));
    }

    /**
     * Called when the feature is released after being pressed down.
     * By default, this method emits an {@link IoFeaturePressEvent}.
     * <p>
     * This method can be overridden to change its behavior. For example,
     * by emitting a different type of event or by updating the observed
     * state.
     */
    protected void onRelease() {
        observer.onNext(new IoFeatureReleaseEvent(device, feature));
    }

    private void firePressEvents(long currentTime) {
        boolean pressed = this.isPressed();
        boolean wasPressed = this.pressed;
        this.pressed = pressed;

        if (!wasPressed && pressed) {
            this.lastPressTime = currentTime;
            this.onPress();
        } else if (wasPressed && !pressed) {
            this.onRelease();
            this.held = false;
        }
    }

    private void fireHoldEvents(long currentTime) {
        if (!this.pressed) {
            return;
        }

        PressableIoFeatureConfigView config = configSupplier.get();

        boolean holdEnabled = config.isHoldEnabled();
        boolean holdPressEnabled = config.isHoldPressEnabled();
        long holdTime = config.getHoldTime();
        long holdPressInterval = config.getHoldPressInterval();

        long pressDuration = currentTime - this.lastPressTime;
        if (holdEnabled && !held && pressDuration >= holdTime) {
            this.held = true;
            this.onHold();
        } else if (!holdEnabled) {
            this.held = false;
        }

        long lastHeldPressDuration = currentTime - this.lastPressTime;
        if (holdPressEnabled && held
                && lastHeldPressDuration >= holdPressInterval) {
            this.lastPressTime = currentTime;
            this.onPress();
        }
    }

    /**
     * Performs a <i>single</i> query on the pressable state and emits all
     * necessary events from the I/O device. It is recommended to call this
     * method once every application update.
     */
    @MustBeInvokedByOverriders
    public void poll() {
        long currentTime = System.currentTimeMillis();
        this.firePressEvents(currentTime);
        this.fireHoldEvents(currentTime);
    }

}
