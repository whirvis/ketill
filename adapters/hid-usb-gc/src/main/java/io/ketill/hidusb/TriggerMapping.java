package io.ketill.hidusb;

public class TriggerMapping {

    public final int gcAxis;
    public final int min, max;

    public TriggerMapping(int gcAxis, int min, int max) {
        this.gcAxis = gcAxis;
        this.min = min;
        this.max = max;
    }

}
