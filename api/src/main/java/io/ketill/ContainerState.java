package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Contain the internal state of an {@link IoFeature}.
 * <p>
 * <b>Requirements:</b> This class can only be extended by the
 * container state. When extended by the internal state, it will
 * result in an {@code UnsupportedOperationException}.
 *
 * @param <Z> the internal state type.
 * @see AutonomousState
 */
public abstract class ContainerState<Z> {

    /**
     * The internal state which this contains.
     * <p>
     * This should (usually) be kept hidden from the user.
     * Its data should be made visible and/or modifiable via
     * methods that limit interaction.
     */
    protected final @NotNull Z internalState;

    /**
     * Constructs a new {@code ContainerState}.
     *
     * @param internalState the internal state.
     * @throws NullPointerException if {@code internalState}
     *                              is {@code null}.
     */
    public ContainerState(Z internalState) {
        this.internalState = Objects.requireNonNull(internalState,
                "internalState cannot be null");
    }

}
