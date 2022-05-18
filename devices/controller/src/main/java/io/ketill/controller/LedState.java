package io.ketill.controller;

/**
 * Contains the state of a {@link PlayerLed}.
 * <p>
 * <b>Note:</b> This class can be extended to implement extra LED modes.
 * However, if this is done, {@link PlayerLed} cannot be used. A new I/O
 * feature type will be needed to instantiate it.
 *
 * @see #setNumber(int)
 * @see #setPattern(int)
 */
public class LedState {

    public static final int MODE_NUMBER = 0;
    public static final int MODE_PATTERN = 1;

    /* protected access for child classes */
    protected int mode;
    protected int value;

    /**
     * @param number the initial player number.
     */
    public LedState(int number) {
        this.setNumber(number);
    }

    /**
     * Constructs a new {@code PlayerLed1i} with a player number of {@code 1}.
     */
    public LedState() {
        this(1);
    }

    public int getMode() {
        return this.mode;
    }

    /**
     * The return value of {@link #getMode()} dictates how the return value
     * of this method should be interpreted.
     *
     * @return the current value for this LED.
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Sets the player number for the LEDs. This results in the LEDs of the
     * device being enabled or disabled depending on the player number.
     *
     * @param number the player number.
     * @see #setPattern(int)
     */
    public void setNumber(int number) {
        this.mode = MODE_NUMBER;
        this.value = number;
    }

    /**
     * Sets the bit pattern for the LED. This results in the LEDs of the
     * device enabled or disabled based on the bits of {@code pattern}.
     * Bit 0 is LED 1, bit 1 is LED 2, and so on.
     *
     * @param pattern the bit pattern.
     * @see #setNumber(int)
     */
    public void setPattern(int pattern) {
        this.mode = MODE_PATTERN;
        this.value = pattern;
    }

}
