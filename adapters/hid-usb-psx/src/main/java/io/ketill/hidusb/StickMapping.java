package io.ketill.hidusb;

import io.ketill.MappingType;

@MappingType
final class StickMapping {

    public final int byteOffsetX;
    public final int byteOffsetY;
    public final int thumbByteOffset;
    public final int thumbBitIndex;
    public final boolean hasThumb;

    public StickMapping(int byteOffsetX, int byteOffsetY,
                        int thumbByteOffset, int thumbBitIndex) {
        this.byteOffsetX = byteOffsetX;
        this.byteOffsetY = byteOffsetY;
        this.thumbByteOffset = thumbByteOffset;
        this.thumbBitIndex = thumbBitIndex;
        this.hasThumb = thumbByteOffset >= 0 && thumbBitIndex >= 0;
    }

}
