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
 * via {@link PressableState}). When the state of the observed feature
 * changes, an associated event is emitted.
 * <p>
 * <b>Note:</b> For events to be emitted, the monitor must be polled
 * periodically via the {@link #poll()} method. This can be achieved
 * by implementing {@link AutonomousState} in the internal  state of
 * an I/O feature.
 */
public class PressableIoFeatureObserver {

    protected final @NotNull IoFeature<?, ?> feature;
    protected final @NotNull PressableState state;
    protected final @NotNull IoDeviceObserver events;
    protected final @NotNull IoDevice device;
    protected final @NotNull Supplier<PressableIoFeatureConfigView> configSupplier;

    private boolean pressed;
    private boolean held;
    private long lastPressTime;

    /**
     * @param state    the pressable state of {@code feature}.
     * @param feature  the feature whose state is being observed.
     * @param observer the I/O device observer.
     * @throws NullPointerException     if {@code feature}, {@code state},
     *                                  or {@code observer} are {@code null}.
     * @throws IllegalStateException    if {@code feature} is not registered
     *                                  to the device of {@code observer}.
     * @throws IllegalArgumentException if {@code state} does not belong to
     *                                  {@code feature}.
     */
    public PressableIoFeatureObserver(@NotNull IoFeature<?, ?> feature,
                                      @NotNull PressableState state,
                                      @NotNull IoDeviceObserver observer) {
        this.feature = Objects.requireNonNull(feature,
                "feature cannot be null");
        this.state = Objects.requireNonNull(state,
                "state cannot be null");
        this.events = Objects.requireNonNull(observer,
                "observer cannot be null");
        this.device = observer.getDevice();

        if (!device.isFeatureRegistered(feature)) {
            String msg = "feature must be registered to device";
            throw new IllegalStateException(msg);
        } else if (device.getFeature(state) != feature) {
            String msg = "state must belong to feature";
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

    private void firePressEvents(long currentTime) {
        boolean pressed = state.isPressed();
        boolean wasPressed = this.pressed;
        this.pressed = pressed;

        if (!wasPressed && pressed) {
            this.lastPressTime = currentTime;
            events.onNext(new IoFeaturePressEvent(device, feature));
        } else if (wasPressed && !pressed) {
            events.onNext(new IoFeatureReleaseEvent(device, feature));
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
            events.onNext(new IoFeatureHoldEvent(device, feature));
        } else if (!holdEnabled) {
            this.held = false;
        }

        long lastHeldPressDuration = currentTime - this.lastPressTime;
        if (holdPressEnabled && held
                && lastHeldPressDuration >= holdPressInterval) {
            this.lastPressTime = currentTime;
            events.onNext(new IoFeaturePressEvent(device, feature));
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
