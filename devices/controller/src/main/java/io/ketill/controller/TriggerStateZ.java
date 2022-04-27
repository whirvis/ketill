package io.ketill.controller;

import io.ketill.pressable.MonitorUpdatedField;

public class TriggerStateZ {

    public float force;

    /**
     * Adapters should only update the state of {@link #force}. The
     * {@link AnalogTriggerMonitor} handles determining if a trigger
     * is currently pressed or held down.
     */
    @MonitorUpdatedField
    public boolean pressed, held;

}
