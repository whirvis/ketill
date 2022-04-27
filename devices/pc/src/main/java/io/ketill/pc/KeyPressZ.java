package io.ketill.pc;

import io.ketill.pressable.MonitorUpdatedField;

public class KeyPressZ {

    public boolean pressed;

    /**
     * Adapters should only update the state of {@link #pressed}. The
     * {@link KeyboardKeyMonitor} will handle determining if a key is
     * currently held down or not.
     */
    @MonitorUpdatedField
    public boolean held;

}
