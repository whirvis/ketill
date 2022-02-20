package io.ketill.hidusb.gc;

import io.ketill.MappingType;

@MappingType
final class AxisMapping {

    final int gcAxis;
    final int min, max;

    AxisMapping(int gcAxis, int min, int max) {
        this.gcAxis = gcAxis;
        this.min = min;
        this.max = max;
    }

}
