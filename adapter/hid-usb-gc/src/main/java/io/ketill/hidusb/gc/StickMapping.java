package io.ketill.hidusb.gc;

import io.ketill.MappingType;
import org.jetbrains.annotations.NotNull;

@MappingType
final class StickMapping {

    final AxisMapping xAxis;
    final AxisMapping yAxis;

    StickMapping(@NotNull AxisMapping xAxis, @NotNull AxisMapping yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    StickMapping(int gcAxisX, int gcAxisY, int xMin, int xMax, int yMin,
                 int yMax) {
        this(new AxisMapping(gcAxisX, xMin, xMax), new AxisMapping(gcAxisY,
                yMin, yMax));
    }

}
