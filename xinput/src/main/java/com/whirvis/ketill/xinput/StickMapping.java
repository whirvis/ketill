package com.whirvis.ketill.xinput;

import com.github.strikerx3.jxinput.enums.XInputAxis;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class StickMapping {

    public final @NotNull XInputAxis xAxis;
    public final @NotNull XInputAxis yAxis;
    public final @Nullable Field zButtonField;

    public StickMapping(@NotNull XInputAxis xAxis,
                        @NotNull XInputAxis yAxis,
                        @Nullable Field zButtonField) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.zButtonField = zButtonField;
    }

}
