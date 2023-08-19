package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * A dummy type akin to {@link Void}, which indicates an {@link IoState}
 * has no internal data.
 *
 * @see IoState#NO_INTERNALS
 */
public final class NoInternals {

    static final @NotNull NoInternals INSTANCE = new NoInternals();

    private NoInternals() {
        /* prevent instantiation */
    }

    @Override
    public @NotNull String toString() {
        return "no internals";
    }

}