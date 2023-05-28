package io.ketill;

/**
 * Determines when an {@link IoFeature} is bridged.
 *
 * @see IoState
 */
public enum IoFlow {

    /**
     * Obtain the {@link IoFeature}'s state on query.
     * <p>
     * Use this when the {@link IoState} stores data obtained from the
     * device. Examples include (but are not limited to) if a gamepad
     * button is pressed or the position of an analog stick.
     *
     * @see IoDevice#query()
     */
    IN(true, false),

    /**
     * Communicate the {@link IoFeature}'s state on update.
     * <p>
     * Use this when the {@link IoState} stores data communicated to the
     * device. Examples include (but are not limited to) the intensity of
     * a rumble motor or the status of an LED.
     *
     * @see IoDevice#update()
     */
    OUT(false, true),

    /**
     * Combination of {@link #IN} and {@link #OUT}.
     */
    TWO_WAY(true, true);

    private final boolean inward;
    private final boolean outward;

    IoFlow(boolean inward, boolean outward) {
        this.inward = inward;
        this.outward = outward;
    }

    /**
     * Returns if the {@link IoFeature} obtains data.
     *
     * @return {@code true} if the feature's {@link IoState} contains data
     * which is obtained from its device, {@code false} otherwise.
     */
    public boolean flowsInward() {
        return this.inward;
    }

    /**
     * Returns if the {@link IoFeature} communicates data.
     *
     * @return {@code true} if the feature's {@link IoState} contains data
     * which is communicated to its device, {@code false} otherwise.
     */
    public boolean flowsOutward() {
        return this.outward;
    }

}
