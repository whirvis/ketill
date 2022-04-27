package io.ketill.pc;

import io.ketill.pressable.MonitorUpdatedField;

public class MouseClickZ {

    public boolean pressed;

    /**
     * Adapters should only update the state of {@link #pressed}. The
     * {@link MouseButtonMonitor} will handle determining if a mouse
     * button is currently held down or not.
     */
    @MonitorUpdatedField
    public boolean held;

}
