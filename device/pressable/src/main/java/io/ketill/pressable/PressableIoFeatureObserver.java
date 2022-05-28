package io.ketill.pressable;

import io.ketill.AutonomousState;
import io.ketill.IoDevice;
import io.ketill.IoDeviceObserver;
import io.ketill.IoFeature;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * An object which observes the state of an I/O feature, specifically one
 * which can be considered to be pressed or released (this is determined
 * via the {@link #isPressedImpl()} method). The associated event is emitted
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
public abstract class PressableIoFeatureObserver<Z> implements Observer<PressableIoFeatureEvent> {

    /**
     * The feature being observed.
     */
    protected final @NotNull IoFeature<Z, ?> feature;

    /**
     * The internal state of {@link #feature}. This should be used to
     * implement methods like {@link #isPressedImpl()}.
     */
    protected final @NotNull Z internalState;

    /**
     * The device which owns {@link #feature}.
     */
    protected final @NotNull IoDevice device;

    private final IoDeviceObserver observer;
    private final Supplier<PressableIoFeatureConfigView> configSupplier;

    private boolean pressed;
    private boolean held;
    private long lastPressTime;

    /**
     * Constructs a new {@code PressableIoFeatureObserver}.
     * <p>
     * Take note that {@code feature}, {@code internalState}, and the device
     * of {@code observer} are accessible as fields to extending classes. To
     * emit events, use {@link #onNext(PressableIoFeatureEvent)}.
     *
     * @param feature       the feature whose state is being observed.
     * @param internalState the internal state of {@code feature}.
     * @param observer      the I/O device observer.
     * @throws NullPointerException if {@code feature},
     *                              {@code internalState}, or
     *                              {@code observer} are {@code null}.
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

        if (device instanceof PressableIoFeatureSupport) {
            PressableIoFeatureSupport support =
                    (PressableIoFeatureSupport) device;
            this.configSupplier = support::getPressableConfig;
        } else {
            this.configSupplier = () -> PressableIoFeatureConfig.DEFAULT;
        }
    }

    /**
     * Called each time {@link #poll()} is invoked. This method is used to
     * determine if the observed feature is currently held down.
     *
     * @return {@code true} if the feature is currently pressed down,
     * {@code false} otherwise.
     * @see #isPressed()
     */
    protected abstract boolean isPressedImpl();

    /**
     * @return {@code true} if the feature is currently pressed down,
     * {@code false} otherwise.
     */
    public final boolean isPressed() {
        return this.pressed;
    }

    /**
     * @return {@code true} if the feature has been pressed down long enough
     * to be considered held, {@code false} otherwise.
     */
    public final boolean isHeld() {
        return this.held;
    }

    /**
     * @return the configuration this observer uses in determining if and
     * when events should be emitted. If the configuration returned by the
     * device is {@code null}, {@link PressableIoFeatureConfig#DEFAULT} will
     * be returned instead.
     */
    public final @NotNull PressableIoFeatureConfigView getConfig() {
        PressableIoFeatureConfigView config = configSupplier.get();
        if (config == null) {
            return PressableIoFeatureConfig.DEFAULT;
        }
        return config;
    }

    @Override
    public final void onSubscribe(@NotNull Disposable disposable) {
        observer.onSubscribe(disposable);
    }

    /**
     * Provides subscribers with a new event to observe. This method may
     * be called 0 or more times. The event <i>must</i> be triggered by
     * the I/O feature which this observes.
     *
     * @param event the event to emit.
     * @throws NullPointerException     if {@code event} is {@code null}.
     * @throws IllegalArgumentException if {@code event} was constructed
     *                                  to be triggered by a different I/O
     *                                  feature than the one which this
     *                                  observes.
     * @see PressableIoFeatureEvent#getFeature()
     */
    @Override
    public final void onNext(@NotNull PressableIoFeatureEvent event) {
        Objects.requireNonNull(event, "event cannot be null");
        if (event.getFeature() != feature) {
            String msg = "event must be triggered by";
            msg += " the feature which this observes";
            throw new IllegalArgumentException(msg);
        }
        observer.onNext(event);
    }

    @Override
    public final void onError(@NotNull Throwable cause) {
        observer.onError(cause);
    }

    @Override
    public final void onComplete() {
        observer.onComplete();
    }

    /**
     * Called when the feature is first pressed down, or virtually
     * pressed due to being held down. By default, this method emits
     * an {@link IoFeaturePressEvent}.
     */
    protected void onPress() {
        this.onNext(new IoFeaturePressEvent(device, feature));
    }

    /**
     * Called when the feature is first held down, due to being pressed
     * for an extended period of time. By default, this method emits an
     * {@link IoFeatureHoldEvent}.
     */
    protected void onHold() {
        this.onNext(new IoFeatureHoldEvent(device, feature));
    }

    /**
     * Called when the feature is released after being pressed down.
     * By default, this method emits an {@link IoFeatureReleaseEvent}.
     */
    protected void onRelease() {
        this.onNext(new IoFeatureReleaseEvent(device, feature));
    }

    private void emitPressEvents(long currentTime) {
        boolean pressed = this.isPressedImpl();
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

    private void emitHoldEvents(long currentTime) {
        if (!this.pressed) {
            return;
        }

        PressableIoFeatureConfigView config = this.getConfig();

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
        this.emitPressEvents(currentTime);
        this.emitHoldEvents(currentTime);
    }

}
