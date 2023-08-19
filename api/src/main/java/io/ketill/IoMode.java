package io.ketill;

/**
 * Determines when the state of an {@link IoFeature} is bridged.
 *
 * @see IoState
 */
public enum IoMode {

    /**
     * Obtain the {@link IoFeature}'s state on read.
     * <p>
     * Use this when the {@link IoState} stores data obtained from the device.
     * Examples include (but are not limited to) if a gamepad button is pressed
     * or the position of an analog stick.
     *
     * @see IoHandle#read()
     */
    READ,

    /**
     * Communicate the {@link IoFeature}'s state on write.
     * <p>
     * Use this when the {@link IoState} stores data communicated to the device.
     * Examples include (but are not limited to) the intensity of a rumble motor
     * or an LED's display number.
     *
     * @see IoHandle#write()
     */
    WRITE;

}