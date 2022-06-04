package io.ketill.controller;

/**
 * Contains the state of a {@link PlayerLed}.
 * <p>
 * <b>Note:</b> This class can be extended to implement extra LED modes.
 * However, if this is done, {@link PlayerLed} cannot be used. A new I/O
 * feature type will be needed to instantiate it.
 *
 * @see #setNumber(int)
 * @see #setBitPattern(int)
 */
public class LedState {

    /**
     * The default mode for LEDs. In this mode, the pattern is determined
     * by a player number. For example, if {@link #getValue()} returns a
     * value of {@code 3} in this mode, the LEDs on a controller should
     * look like this:
     * <pre>
     *     /---------------\
     *     | 1 | 2 | 3 | 4 |
     *     |   |   | * |   |
     *     \---------------/
     * </pre>
     * <p>
     * <b>Note:</b> If the player number is higher than the LED count, the
     * behavior is undefined. Included adapters (such as {@code hid-usb-psx})
     * will follow an additive pattern in this scenario. For example, if
     * {@link #getValue()} returns a value of {@code 5}:
     * <pre>
     *     /---------------\
     *     | 1 | 2 | 3 | 4 |
     *     | * |   |   | * |
     *     \---------------/
     * </pre>
     * <p>
     * Notice how both the first and fourth LED are activated here. Since
     * {@code 4 + 1} is equal to {@code 5}, this represents player five.
     * However, this is still limited by the LED count. Since the example
     * above only has four LEDs, the maximum is {@code 15}.
     */
    public static final int MODE_NUMBER = 0;

    /**
     * In this mode, the pattern is determined by the activated bits of the
     * current value for the LED. For example, if {@link #getValue()} returns
     * a value of {@code 6} in this mode, the LEDs on a controller should
     * look like this:
     * <pre>
     *     /---------------\
     *     | 1 | 2 | 3 | 4 |
     *     |   | * |   | * |
     *     \---------------/
     * </pre>
     * <p>
     * Notice how the second and fourth LED are activated here. This is
     * due to the fact that, in binary, {@code 6} is {@code 0101}. However,
     * since the return type of {@link #getValue()} is {@code int}, this is
     * limited to 32 LEDs.
     */
    public static final int MODE_BITWISE = 1;

    /**
     * Both {@code mode} and {@code value} are used by the adapter
     * to determine how the LEDs should be set.
     * <p>
     * These are {@code protected} so child classes can add their
     * own setters to modify them accordingly.
     */
    protected int mode, value;

    /**
     * Constructs a new {@code LedState} in {@link #MODE_NUMBER}.
     *
     * @param number the initial player number.
     */
    public LedState(int number) {
        this.setNumber(number);
    }

    /**
     * Constructs a new {@code LedState} in {@link #MODE_NUMBER} with
     * an initial player number of {@code 1}.
     */
    public LedState() {
        this(1);
    }

    /**
     * Returns the current mode of this LED state.
     * <p>
     * This determines how an adapter will interpret {@link #getValue()}.
     *
     * @return the current mode of this LED state.
     */
    public int getMode() {
        return this.mode;
    }

    /**
     * Returns the current value for this LED.
     * <p>
     * <b>Note:</b> The return value of {@link #getMode()} dictates how
     * an adapter will interpret the return value of this method.
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
     * @see #MODE_NUMBER
     * @see #setBitPattern(int)
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
     * @see #MODE_BITWISE
     * @see #setNumber(int)
     */
    public void setBitPattern(int pattern) {
        this.mode = MODE_BITWISE;
        this.value = pattern;
    }

}
