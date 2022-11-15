package io.ketill;

/**
 * TODO: add proper explanation
 */
public enum IoFlow {

    /**
     * Write to the {@link IoState} on query.
     * <p>
     * This is suitable for I/O features which pull data from a device.
     * Examples of this include (but are not limited to): gamepad buttons,
     * analog sticks, analog triggers, gyroscopes, etc.
     *
     * @see IoDevice#query()
     */
    IN,

    /**
     * Read from the {@link IoState} on update.
     * <p>
     * This is suitable for I/O features which push data to a device.
     * Examples of this include (but are not limited to): rumble motors,
     * LED indicators, sound speakers, etc.
     *
     * @see IoDevice#update()
     */
    OUT,

}
