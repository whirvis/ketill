package io.ketill.xinput;

import io.ketill.MappingType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@MappingType
final class StickMapping {

    final @NotNull XInputAxisAccessor xAxis;
    final @NotNull XInputAxisAccessor yAxis;
    final @Nullable XInputButtonAccessor zButton;

    StickMapping(@NotNull XInputAxisAccessor xAxis,
                 @NotNull XInputAxisAccessor yAxis,
                 @Nullable XInputButtonAccessor zButton) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.zButton = zButton;
    }

}
