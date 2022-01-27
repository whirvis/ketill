package com.whirvis.kibasan.dualshock;

public class Ds4ButtonMapping {

    public final int byteOffset;
    public final int bitIndex;

    public Ds4ButtonMapping(int byteOffset, int bitIndex) {
        this.byteOffset = byteOffset;
        this.bitIndex = bitIndex;
    }

}
