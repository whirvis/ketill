package io.ketill.psx;

import io.ketill.IoDeviceSeeker;
import io.ketill.IoDeviceSeekerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDeviceSeeker} when the state of ambiguity between
 * PlayStation controllers has changed.
 *
 * @see PsxAmbiguityCandidate
 */
public final class PsxAmbiguityEvent extends IoDeviceSeekerEvent {

    private final boolean nowAmbiguous;

    /**
     * @param seeker       the seeker which emitted this event.
     * @param nowAmbiguous {@code true} if there is now an ambiguity
     *                     between PlayStation controllers, {@code false}
     *                     if it has been resolved.
     * @throws NullPointerException if {@code seeker} is {@code null}.
     */
    public PsxAmbiguityEvent(@NotNull IoDeviceSeeker<? extends PsxController> seeker,
                             boolean nowAmbiguous) {
        super(seeker);
        this.nowAmbiguous = nowAmbiguous;
    }

    /**
     * @return {@code true} if there is now an ambiguity between PlayStation
     * controllers, {@code false} if it has been resolved.
     */
    public boolean isNowAmbiguous() {
        return this.nowAmbiguous;
    }

}
