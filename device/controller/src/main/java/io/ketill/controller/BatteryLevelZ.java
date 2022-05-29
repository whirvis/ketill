package io.ketill.controller;

/**
 * Contains the state of an {@link InternalBattery}.
 */
public class BatteryLevelZ {

    /**
     * This should be updated by the adapter to store the current
     * battery level. This value should be on a scale of {@code 0.0F}
     * to {@code 1.0F} (unless unknown).
     * <p>
     * <b>Note:</b> If unknown, this should be {@code -1.0F}.
     */
    public float level;

    BatteryLevelZ() {
        this.level = -1.0F;
    }

}
