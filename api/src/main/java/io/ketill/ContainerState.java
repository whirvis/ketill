package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A utility class for classes which contain the internal state of
 * an {@link IoFeature}.
 * <p>
 * <b>Note:</b> This class can be extended by the container state
 * only. When extended by the internal state, it will result in
 * an {@code UnsupportedOperationException} during creation.
 *
 * @param <Z> the internal state type.
 * @see AutonomousState
 */
public abstract class ContainerState<Z> {

    protected final @NotNull Z internalState;

    /**
     * @param internalState the internal state.
     * @throws NullPointerException if {@code internalState} is {@code null}.
     */
    public ContainerState(Z internalState) {
        this.internalState = Objects.requireNonNull(internalState,
                "internalState cannot be null");
    }

}