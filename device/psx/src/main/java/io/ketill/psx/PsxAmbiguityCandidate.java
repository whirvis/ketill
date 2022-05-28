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
     * <b>Note:</b> Conditions for ambiguity between controllers are unique,
     * depending on both the physical device and the capabilities of the I/O
     * device seeker.
     *
     * @return {@code true} if there is currently ambiguity between the
     * discovered PlayStation controllers, {@code false} otherwise.
     */
    boolean isAmbiguous();

}
