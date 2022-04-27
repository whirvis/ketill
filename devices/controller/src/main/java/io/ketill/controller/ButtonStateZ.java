package io.ketill.controller;

import io.ketill.pressable.MonitorUpdatedField;

public class ButtonStateZ {

    public boolean pressed;

    /**
     * Adapters should only update the state of {@link #pressed}. The
     * {@link DeviceButtonMonitor} will handle determining if a button
     * is currently held down or not.
     */
    @MonitorUpdatedField
    public boolean held;

}
