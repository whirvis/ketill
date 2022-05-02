package io.ketill.psx;

import io.ketill.IoDeviceSeeker;
import org.jetbrains.annotations.NotNull;

/**
 * An interface used by {@link PsxAmbiguityCallback} to notify listeners when
 * the state of ambiguity between PlayStation controllers has changed.
 *
 * @param <C> the PlayStation controller type.
 * @see PsxAmbiguityCandidate#onAmbiguity(PsxAmbiguityCallback)
 */
@FunctionalInterface
public interface PsxAmbiguityCallback<C extends PsxController> {

    /**
     * Called when the state of ambiguity between PlayStation controllers has
     * changed.
     *
     * @param seeker    the I/O device seeker which detected the change.
     * @param ambiguous {@code true} if there is now an ambiguity between
     *                  PlayStation controllers, {@code false} if it has
     *                  been resolved.
     */
    void execute(@NotNull IoDeviceSeeker<? extends C> seeker,
                 boolean ambiguous);

}
