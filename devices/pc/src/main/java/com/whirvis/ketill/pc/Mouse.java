package com.whirvis.ketill.pc;

import com.whirvis.ketill.AdapterSupplier;
import com.whirvis.ketill.Button1bc;
import com.whirvis.ketill.FeaturePresent;
import com.whirvis.ketill.FeatureState;
import com.whirvis.ketill.IoDevice;
import org.jetbrains.annotations.NotNull;

public class Mouse extends IoDevice {

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
    public final @NotNull Button1bc
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
    public final @NotNull Button1bc
            left = m1,
            right = m2,
            middle = m3;

    @FeatureState
    public final @NotNull Cursor2f
            cursor = this.getState(FEATURE_CURSOR);
    /* @formatter:on */

    public Mouse(@NotNull AdapterSupplier<Mouse> adapterSupplier) {
        super("mouse", adapterSupplier);
    }

}
