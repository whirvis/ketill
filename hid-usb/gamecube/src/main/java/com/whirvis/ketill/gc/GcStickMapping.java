package com.whirvis.ketill.gc;

public class GcStickMapping {

    public final int gcAxisX;
    public final int gcAxisY;
    public final int xMin, xMax;
    public final int yMin, yMax;

    public GcStickMapping(int gcAxisX, int gcAxisY, int xMin, int xMax,
                          int yMin, int yMax) {
        this.gcAxisX = gcAxisX;
        this.gcAxisY = gcAxisY;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

}
