package io.ketill.psx;

import org.jetbrains.annotations.Nullable;

/**
 * Some PlayStation controllers, such as the DualShock 4 controller, have a
 * unique (and extremely annoying) trait. This trait causes them to report
 * themselves as multiple devices at the same time (e.g., as both a USB and
 * Bluetooth controller if connected via both). Sometimes, it is impossible
 * to determine which controllers are which in the physical world. As such,
 * an <i>ambiguity</i> occurs.
 *
 * @param <C> the PlayStation controller type.
 * @see #isAmbiguous()
 * @see #onAmbiguity(PsxAmbiguityCallback)
 */
public interface PsxAmbiguityCandidate<C extends PsxController> {

    /**
     * <b>Note:</b> Conditions for ambiguity between controllers are unique,
     * depending on both the physical device and the capabilities of the I/O
     * device seeker.
     *
     * @return {@code true} if there is currently ambiguity between the
     * discovered PlayStation controllers, {@code false} otherwise.
     * @see #onAmbiguity(PsxAmbiguityCallback)
     */
    boolean isAmbiguous();

    /**
     * Sets the callback for when the state of ambiguity between PlayStation
     * controllers has changed.
     *
     * @param callback the code execute when the state of ambiguity has
     *                 changed. A value of {@code null} is permitted, and
     *                 will result in nothing being executed.
     * @see #isAmbiguous()
     */
    void onAmbiguity(@Nullable PsxAmbiguityCallback<C> callback);

}
