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

    /**
     * The internal state which this state contains. This should be kept
     * hidden from the user. Its data made visible and/or modifiable via
     * special methods that limit how it can be interacted with.
     */
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
