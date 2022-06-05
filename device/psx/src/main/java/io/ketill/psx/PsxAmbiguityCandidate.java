package io.ketill.psx;

/**
 * Some PlayStation controllers, such as the DualShock 4 controller, have a
 * unique (and extremely annoying) trait. This trait causes them to report
 * themselves as multiple devices at the same time (e.g., as both a USB and
 * Bluetooth controller if connected via both). It is sometimes impossible
 * to determine which controllers are which in the physical world. As such,
 * an <i>ambiguity</i> occurs.
 * <p>
 * Changes in the state of ambiguity between PlayStation controllers can be
 * listened for by subscribing to the {@link PsxAmbiguityEvent}. For example:
 * <pre>
 * seeker.subscribeEvents(PsxAmbiguityEvent.class,
 *     event -> {
 *     /&#42; handle event &#42;/
 * });
 * </pre>
 *
 * @see #isAmbiguous()
 */
public interface PsxAmbiguityCandidate {

    /**
     * Returns if there is currently an ambiguity between the discovered
     * PlayStation controllers.
     * <p>
     * Conditions for ambiguity between these controllers are unique. They
     * depend on both the characteristics of the physical device and the
     * capabilities of the seeker.
     *
     * @return {@code true} if there is currently an ambiguity between the
     * discovered PlayStation controllers, {@code false} otherwise.
     */
    boolean isAmbiguous();

}
