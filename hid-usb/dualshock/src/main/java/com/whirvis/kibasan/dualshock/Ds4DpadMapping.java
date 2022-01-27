package com.whirvis.kibasan.dualshock;

public class Ds4DpadMapping {

    public final int byteOffset;
    public final int[] patterns;

    public Ds4DpadMapping(int byteOffset, int... patterns) {
        this.byteOffset = byteOffset;
        this.patterns = patterns;
    }

    public boolean hasPattern(int bits) {
        for (int pattern : patterns) {
            if (pattern == bits) {
                return true;
            }
        }
        return false;
    }

}
