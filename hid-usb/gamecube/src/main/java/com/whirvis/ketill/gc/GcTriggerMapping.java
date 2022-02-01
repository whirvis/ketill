package com.whirvis.ketill.gc;

public class GcTriggerMapping {

    public final int gcAxis;
    public final int min, max;

    public GcTriggerMapping(int gcAxis, int min, int max) {
        this.gcAxis = gcAxis;
        this.min = min;
        this.max = max;
    }

}
