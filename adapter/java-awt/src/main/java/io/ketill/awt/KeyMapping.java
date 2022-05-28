package io.ketill.awt;

import io.ketill.MappingType;

@MappingType
final class KeyMapping {

    final int keyCode;
    final int keyLocation;

    KeyMapping(int keyCode, int keyLocation) {
        this.keyCode = keyCode;
        this.keyLocation = keyLocation;
    }

}
