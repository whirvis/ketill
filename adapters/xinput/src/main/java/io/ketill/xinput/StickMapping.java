package io.ketill.xinput;

import com.github.strikerx3.jxinput.enums.XInputAxis;
import io.ketill.MappingType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@MappingType
final class StickMapping {

    final @NotNull XInputAxis xAxis;
    final @NotNull XInputAxis yAxis;
    final @Nullable XInputButton zButton;

    StickMapping(@NotNull XInputAxis xAxis, @NotNull XInputAxis yAxis,
                 @Nullable XInputButton zButton) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.zButton = zButton;
    }

}
