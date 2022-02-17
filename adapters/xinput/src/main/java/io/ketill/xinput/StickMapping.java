package io.ketill.xinput;

import com.github.strikerx3.jxinput.enums.XInputAxis;
import io.ketill.MappingType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

@MappingType
final class StickMapping {

    final @NotNull XInputAxis xAxis;
    final @NotNull XInputAxis yAxis;
    final @Nullable Field zButtonField;

    StickMapping(@NotNull XInputAxis xAxis, @NotNull XInputAxis yAxis,
                 @Nullable Field zButtonField) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.zButtonField = zButtonField;
    }

}
