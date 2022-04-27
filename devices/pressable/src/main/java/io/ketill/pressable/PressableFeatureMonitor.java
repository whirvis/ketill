package io.ketill.pressable;

import io.ketill.IoDevice;
import io.ketill.IoFeature;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An object which monitors the state of an I/O feature, specifically one
 * which can be considered to be pressed or released (this is determined
 * via {@link #isPressed()}). When an event relating to the monitored
 * feature occurs, a {@link PressableFeatureEvent} is fired.
 * <p>
 * Some feature monitors require special data to be contained inside fired
 * events in order for them to be useful. This data can be provided via
 * {@link #getEventData()} if needed.
 * <p>
 * <b>Note:</b> For events to be fired, the monitor must be polled
 * periodically via the {@link #poll()} method. It is recommended to
 * poll the monitor right after polling the device.
 *
 * @param <F> the I/O feature type.
 * @param <Z> the internal state type.
 * @see MonitorUpdatedField
 */
public abstract class PressableFeatureMonitor<F extends IoFeature<Z, ?>, Z> {

    public final @NotNull IoDevice device;
    public final @NotNull F feature;
    public final @NotNull Z internalState;

    private final @NotNull PressableFeatureSupport support;
    private final @NotNull Supplier<@Nullable Consumer<PressableFeatureEvent>> callbackSupplier;

    private boolean pressed;
    private boolean held;
    private long lastPressTime;

    /**
     * <b>Note:</b> Extending classes wishing to listen for events should
     * override {@link #eventFired(PressableFeatureEvent)}. The callback
     * returned by {@code callbackSupplier} is for users.
     *
     * @param device           the device which owns {@code feature}.
     * @param feature          the feature to monitor.
     * @param internalState    the internal state of {@code feature}.
     * @param callbackSupplier the callback supplier. This can usually just
     *                         be {@code () -> pressableCallback}.
     * @param <I>              the I/O device type, which must also
     *                         implement {@link PressableFeatureSupport}.
     * @throws NullPointerException     if {@code device}, {@code feature},
     *                                  or {@code callbackSupplier} are
     *                                  {@code null}.
     * @throws IllegalStateException    if {@code feature} is not registered
     *                                  to {@code device}.
     * @throws IllegalArgumentException if {@code internalState} does not
     *                                  belong to {@code feature}.
     */
    /* @formatter:off */
    public <I extends IoDevice & PressableFeatureSupport>
            PressableFeatureMonitor(@NotNull I device, @NotNull F feature,
                                    @NotNull Z internalState,
                                    @NotNull Supplier<@Nullable Consumer<PressableFeatureEvent>> callbackSupplier) {
        this.device = Objects.requireNonNull(device,
                "device cannot be null");
        this.feature = Objects.requireNonNull(feature,
                "feature cannot be null");
        this.internalState = Objects.requireNonNull(internalState,
                "internalState cannot be null");

        if(!device.isFeatureRegistered(feature)) {
            String msg = "feature must be registered to device";
            throw new IllegalStateException(msg);
        } else if(device.getFeature(internalState) != feature) {
            String msg = "internalState must belong to feature";
            throw new IllegalArgumentException(msg);
        }

        this.support = device;
        this.callbackSupplier = Objects.requireNonNull(callbackSupplier,
                "callbackSupplier cannot be null");
    }
    /* @formatter:on */

    /**
     * <b>Note:</b> This method returns up-to-date values without the need to
     * call {@link #poll()}.
     *
     * @return {@code true} if the monitored feature is currently pressed,
     * {@code false} otherwise.
     */
    protected abstract boolean isPressed();

    /**
     * This method is called once every time an event is fired. By default,
     * this method will return {@code null} unless it is overridden.
     *
     * @return the event data to send with the fired event.
     */
    protected @Nullable Object getEventData() {
        return null;
    }

    private void fireEvent(@NotNull PressableFeatureEventType type) {
        Object data = this.getEventData();
        PressableFeatureEvent event = new PressableFeatureEvent(type, device,
                feature, held, data);
        this.eventFired(event);

        support.firedPressableEvent(event);
        Consumer<PressableFeatureEvent> callback = callbackSupplier.get();
        if (callback != null) {
            callback.accept(event);
        }
    }

    /**
     * Called when an event is fired. Overriding this method allows for
     * a pressable feature monitor to know when an event has been fired
     * without needing to supply themselves as the callback.
     *
     * @param event the fired event.
     */
    @SuppressWarnings("unused")
    protected void eventFired(@NotNull PressableFeatureEvent event) {
        /* optional implement */
    }

    private void firePressEvents(long currentTime) {
        boolean pressed = this.isPressed();
        boolean wasPressed = this.pressed;
        this.pressed = pressed;

        if (!wasPressed && pressed) {
            this.lastPressTime = currentTime;
            this.fireEvent(PressableFeatureEventType.PRESS);
        } else if (wasPressed && !pressed) {
            this.fireEvent(PressableFeatureEventType.RELEASE);
            this.held = false;
        }
    }

    private void fireHoldEvents(long currentTime) {
        if (!this.pressed) {
            return;
        }

        PressableFeatureConfigView config = support.getPressableConfig();
        Objects.requireNonNull(config,
                "getPressableConfig() cannot return null");

        boolean holdEnabled = config.isHoldEnabled();
        boolean holdPressEnabled = config.isHoldPressEnabled();
        long holdTime = config.getHoldTime();
        long holdPressInterval = config.getHoldPressInterval();

        long pressDuration = currentTime - this.lastPressTime;
        if (holdEnabled && !held && pressDuration >= holdTime) {
            this.held = true;
            this.fireEvent(PressableFeatureEventType.HOLD);
        } else if (!holdEnabled) {
            this.held = false;
        }

        long lastHeldPressDuration = currentTime - this.lastPressTime;
        if (holdPressEnabled && held
                && lastHeldPressDuration >= holdPressInterval) {
            this.lastPressTime = currentTime;
            this.fireEvent(PressableFeatureEventType.PRESS);
        }
    }

    /**
     * Performs a <i>single</i> poll on this feature monitor. Any events
     * relating to the monitored feature will be fired by this method.
     * It is recommended to call this method after polling the device.
     * <p>
     * <b>Note:</b> Extending classes can listen for events fired by
     * overriding the {@link #eventFired(PressableFeatureEvent)} method.
     *
     * @see #isPressed()
     */
    @MustBeInvokedByOverriders
    public void poll() {
        long currentTime = System.currentTimeMillis();
        this.firePressEvents(currentTime);
        this.fireHoldEvents(currentTime);
    }

}
