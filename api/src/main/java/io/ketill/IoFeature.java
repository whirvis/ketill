package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * I/O features provide a definition of a capability present on an
 * {@link IoDevice}. Examples include (but are not limited to), a
 * button, an analog stick, a rumble motor, or an LED indicator.
 *
 * @param <Z> the internal state type. This is visible to {@link IoDevice},
 *            {@link IoDeviceAdapter}, and {@link FeatureRegistry}. This
 *            should remain hidden from the user. It contains sensitive
 *            data for the purposes of the device and the adapter.
 * @param <S> the state container type. This is publicly visible. It should
 *            act as a pass through to the internal state. If the internal
 *            state contains no sensitive data, they can be the same type.
 * @see IoDevice#registerFeature(IoFeature)
 * @see PlainIoFeature
 * @see FeaturePresent
 * @see FeatureState
 */
public abstract class IoFeature<Z, S> {

    public final @NotNull String id;

    /**
     * @param id the feature ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public IoFeature(@NotNull String id) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        if (id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be empty");
        } else if (!id.matches("\\S+")) {
            throw new IllegalArgumentException("id cannot contain whitespace");
        }
    }

    /**
     * Constructs a new {@code StateContainer} containing a newly
     * instantiated instance of both the internal and container state.
     *
     * @return the state container.
     * @throws NullPointerException          if {@link #getInternalState()} or
     *                                       {@link #getContainerState(Object)}
     *                                       return {@code null}.
     * @throws UnsupportedOperationException if the returned internal state
     *                                       or container state are instances
     *                                       of an {@link IoFeature}.
     */
    protected final @NotNull StatePair<Z, S> getState() {
        Z internalState = this.getInternalState();
        Objects.requireNonNull(internalState,
                "getInternalState() cannot return null");
        if (internalState instanceof IoFeature) {
            String msg = "internal state cannot be an";
            msg += " " + this.getClass().getSimpleName();
            throw new UnsupportedOperationException(msg);
        }

        S containerState = this.getContainerState(internalState);
        Objects.requireNonNull(containerState,
                "getContainerState() cannot return null");
        if (containerState instanceof IoFeature) {
            String msg = "container state cannot be an";
            msg += " " + this.getClass().getSimpleName();
            throw new UnsupportedOperationException(msg);
        }

        return new StatePair<>(internalState, containerState);
    }

    /**
     * <b>Note:</b> This cannot be an {@link IoFeature} instance. This is
     * to prevent possible headaches with other methods.
     *
     * @return a newly instantiated instance of the internal state. This
     * method must <i>never</i> return {@code null}.
     */
    protected abstract @NotNull Z getInternalState();

    /**
     * <b>Note:</b> This cannot be an {@link IoFeature} instance. This is
     * to prevent possible headaches with other methods.
     *
     * @param internalState the internal state. This can be used to access
     *                      sensitive data without while limiting the user
     *                      as necessary (e.g., making it read only).
     * @return a newly instantiated instance of the container state. This
     * method must <i>never</i> return {@code null}.
     */
    protected abstract @NotNull S getContainerState(@NotNull Z internalState);

    /**
     * Updates the internal state of this feature <i>after</i> it has been
     * updated by the adapter of an I/O device. By default, this method does
     * nothing.
     * <p>
     * If necessary, this should be used to update the internal state of a
     * feature where the adapter is not considered responsible (e.g., the
     * calibration of an analog stick).
     *
     * @param internalState the internal state of the feature.
     * @param events        an observer which can emit events to
     *                      subscribers of the I/O device.
     */
    protected void update(@NotNull Z internalState,
                          @NotNull IoDeviceObserver events) {
        /* optional implement */
    }

}
