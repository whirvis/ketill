package io.ketill.hidusb.psx;

import io.ketill.MappingType;

@MappingType
final class ButtonMapping {

    final int byteOffset;
    final int bitIndex;

    ButtonMapping(int byteOffset, int bitIndex) {
        this.byteOffset = byteOffset;
        this.bitIndex = bitIndex;
    }

}
