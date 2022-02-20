package io.ketill.hidusb.psx;

import io.ketill.MappingType;

@MappingType
final class DpadMapping {

    final int byteOffset;
    final int[] patterns;

    DpadMapping(int byteOffset, int... patterns) {
        this.byteOffset = byteOffset;
        this.patterns = patterns;
    }

    boolean hasPattern(int bits) {
        for (int pattern : patterns) {
            if (pattern == bits) {
                return true;
            }
        }
        return false;
    }

}
