package io.ketill.pc;

import io.ketill.AdapterSupplier;
import io.ketill.FeaturePresent;
import io.ketill.FeatureState;
import io.ketill.IoDevice;
import io.ketill.ToStringUtils;
import io.ketill.pressable.PressableIoFeatureConfig;
import io.ketill.pressable.PressableIoFeatureConfigView;
import io.ketill.pressable.PressableIoFeatureSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A generic computer mouse.
 *
 * @see Keyboard
 */
public class Mouse extends IoDevice
        implements PressableIoFeatureSupport {

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
    public static final @NotNull MouseCursor
            FEATURE_CURSOR = new MouseCursor("mouse_cursor");
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

    private @NotNull PressableIoFeatureConfigView pressableConfig;

    /**
     * Constructs a new {@code Mouse}.
     *
     * @param adapterSupplier the mouse adapter supplier.
     * @throws NullPointerException if {@code adapterSupplier} is
     *                              {@code null}; if the adapter given by
     *                              {@code adapterSupplier} is {@code null}.
     */
    public Mouse(@NotNull AdapterSupplier<Mouse> adapterSupplier) {
        super("mouse", adapterSupplier);
        this.pressableConfig = PressableIoFeatureConfig.DEFAULT;
    }

    @Override
    public final @NotNull PressableIoFeatureConfigView getPressableConfig() {
        return this.pressableConfig;
    }

    @Override
    public final void usePressableConfig(@Nullable PressableIoFeatureConfigView view) {
        this.pressableConfig = PressableIoFeatureConfig.valueOf(view);
    }

    /* @formatter:off */
    @Override
    public String toString() {
        return ToStringUtils.getJoiner(super.toString(), this)
                .add("pressableConfig=" + pressableConfig)
                .toString();
    }
    /* @formatter:on */

}
