package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A utility class for classes which contain the internal state of an
 * {@link IoFeature}.
 *
 * @param <Z> the internal state type.
 */
public abstract class StateContainer<Z> {

    protected final @NotNull Z internalState;

    /**
     * @param internalState the internal state.
     * @throws NullPointerException if {@code internalState} is {@code null}.
     */
    public StateContainer(Z internalState) {
        this.internalState = Objects.requireNonNull(internalState,
                "internalState cannot be null");
    }

}
