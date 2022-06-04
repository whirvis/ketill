package io.ketill.controller;

import io.ketill.ContainerState;

/**
 * Read-only view of a battery level.
 */
public class BatteryLevel extends ContainerState<BatteryLevelZ> {

    BatteryLevel(BatteryLevelZ internalState) {
        super(internalState);
    }

    /**
     * Returns the current battery level.
     *
     * @return the current battery level on a scale from {@code 0.0F}
     * to {@code 1.0F}. A negative value indicates that it is currently
     * unknown.
     */
    public float getLevel() {
        return internalState.level;
    }

    /**
     * Returns the current battery level percentage.
     *
     * @return the current battery level percentage on a scale from
     * {@code 0} to {@code 100}. A negative value indicates that it is
     * currently unknown.
     */
    public int getPercent() {
        float level = this.getLevel();

        /*
         * If the battery level is unknown, this method will return a
         * value of -1.0F. This would result in this method returning
         * a value of -100. This doesn't make much sense to a caller
         * that doesn't know what's going on under the hood. Instead,
         * simply return a value of -1.
         */
        if (level < 0) {
            return -1;
        }

        float percent = level * 100.0F;
        return Math.round(percent);
    }

}
