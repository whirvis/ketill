package io.ketill.pc;

import io.ketill.AdapterSupplier;
import io.ketill.FeaturePresent;
import io.ketill.FeatureState;
import io.ketill.IoDevice;
import io.ketill.IoFeature;
import io.ketill.RegisteredFeature;
import io.ketill.pressable.PressableFeatureConfig;
import io.ketill.pressable.PressableFeatureConfigView;
import io.ketill.pressable.PressableFeatureEvent;
import io.ketill.pressable.PressableFeatureMonitor;
import io.ketill.pressable.PressableFeatureSupport;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A generic computer mouse.
 *
 * @see Keyboard
 */
@SuppressWarnings("SynchronizeOnNonFinalField")
public class Mouse extends IoDevice
        implements PressableFeatureSupport {

    /* @formatter:off */
    @FeaturePresent
    public static final @NotNull MouseButton
            BUTTON_M1 = new MouseButton("mouse_1"),
            BUTTON_M2 = new MouseButton("mouse_2"),
            BUTTON_M3 = new MouseButton("mouse_3"),
            BUTTON_M4 = new MouseButton("mouse_4"),
            BUTTON_M5 = new MouseButton("mouse_5"),
            BUTTON_M6 = new MouseButton("mouse_6"),
            BUTTON_M7 = new MouseButton("mouse_7"),
            BUTTON_M8 = new MouseButton("mouse_8");

    @FeaturePresent
    public static final @NotNull Cursor
            FEATURE_CURSOR = new Cursor("mouse_cursor");
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState
    public final @NotNull MouseClick
            m1 = this.getState(BUTTON_M1),
            m2 = this.getState(BUTTON_M2),
            m3 = this.getState(BUTTON_M3),
            m4 = this.getState(BUTTON_M4),
            m5 = this.getState(BUTTON_M5),
            m6 = this.getState(BUTTON_M6),
            m7 = this.getState(BUTTON_M7),
            m8 = this.getState(BUTTON_M8);

    /**
     * Aliases for {@link #m1}, {@link #m2}, and {@link #m3}.
     */
    @FeatureState
    public final @NotNull MouseClick
            left = m1,
            right = m2,
            middle = m3;

    @FeatureState
    public final @NotNull CursorState
            cursor = this.getState(FEATURE_CURSOR);
    /* @formatter:on */

    private List<PressableFeatureMonitor<?, ?>> monitors;
    private @NotNull PressableFeatureConfigView pressableConfig;

    private @Nullable Consumer<PressableFeatureEvent> pressableCallback;

    /**
     * @param adapterSupplier the mouse adapter supplier.
     * @throws NullPointerException if {@code adapterSupplier} is
     *                              {@code null}; if the adapter given by
     *                              {@code adapterSupplier} is {@code null}.
     */
    public Mouse(@NotNull AdapterSupplier<Mouse> adapterSupplier) {
        super("mouse", adapterSupplier);
        this.pressableConfig = PressableFeatureConfig.DEFAULT;
    }

    @Override
    protected void featureRegistered(@NotNull RegisteredFeature<?, ?, ?> registered,
                                     @NotNull Object internalState) {
        /*
         * Due to the order of class initialization, the monitors list must
         * be initialized here (otherwise, it would be final and initialized
         * in the constructor.) This is because features can be registered
         * during construction of the super class.
         */
        if (monitors == null) {
            this.monitors = new ArrayList<>();
        }

        if (registered.feature instanceof MouseButton) {
            synchronized (monitors) {
                MouseButton button = (MouseButton) registered.feature;
                MouseClickZ click = (MouseClickZ) internalState;
                monitors.add(new MouseButtonMonitor(this, button,
                        click, () -> pressableCallback));
            }
        }
    }

    @Override
    @MustBeInvokedByOverriders
    protected void featureUnregistered(@NotNull IoFeature<?, ?> feature) {
        monitors.removeIf(monitor -> monitor.feature == feature);
    }

    protected final @Nullable Consumer<PressableFeatureEvent> getPressableCallback() {
        return this.pressableCallback;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Note:</b> Classes extending {@code Mouse} can access the
     * callback set here via {@link #getPressableCallback()}.
     */
    @Override
    public final void onPressableEvent(@Nullable Consumer<PressableFeatureEvent> callback) {
        this.pressableCallback = callback;
    }

    @Override
    public final void usePressableConfig(@Nullable PressableFeatureConfigView view) {
        this.pressableConfig = PressableFeatureConfig.valueOf(view);
    }

    @Override
    public final @NotNull PressableFeatureConfigView getPressableConfig() {
        return this.pressableConfig;
    }

    @Override
    @MustBeInvokedByOverriders
    public void poll() {
        super.poll();
        synchronized (monitors) {
            for (PressableFeatureMonitor<?, ?> monitor : monitors) {
                monitor.poll();
            }
        }
    }

}
