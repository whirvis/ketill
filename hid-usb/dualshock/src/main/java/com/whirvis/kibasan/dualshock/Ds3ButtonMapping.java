package com.whirvis.kibasan.dualshock;

public class Ds3ButtonMapping {

    public final int byteOffset;
    public final int bitIndex;

    public Ds3ButtonMapping(int byteOffset, int bitOffset) {
        this.byteOffset = byteOffset;
        this.bitIndex = bitOffset;
    }

}
