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
 * @see AutonomousState
 * @see ContainerState
 * @see PlainIoFeature
 * @see FeaturePresent
 * @see FeatureState
 */
public abstract class IoFeature<Z, S> {

    private final @NotNull Class<? extends IoDevice> deviceType;
    private final @NotNull String id;

    /**
     * Constructs a new {@code IoFeature}.
     *
     * @param deviceType the type an {@link IoDevice} must be for it to
     *                   create an instance of this feature's state.
     * @param id         the feature ID.
     * @throws NullPointerException     if {@code deviceType} or
     *                                  {@code id} are {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or
     *                                  contains whitespace.
     */
    public IoFeature(@NotNull Class<? extends IoDevice> deviceType,
                     @NotNull String id) {
        this.deviceType = Objects.requireNonNull(deviceType,
                "deviceType cannot be null");
        this.id = Objects.requireNonNull(id, "id cannot be null");
        if (id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be empty");
        } else if (!id.matches("\\S+")) {
            throw new IllegalArgumentException("id cannot contain whitespace");
        }
    }

    /**
     * Constructs a new {@code IoFeature} which can be registered by any
     * type of {@link IoDevice}.
     *
     * @param id the feature ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or
     *                                  contains whitespace.
     */
    public IoFeature(@NotNull String id) {
        this(IoDevice.class, id);
    }

    /**
     * @return the type an {@link IoDevice} must be for it to create an
     * instance of this feature's state.
     */
    public @NotNull Class<? extends IoDevice> getDeviceType() {
        return this.deviceType;
    }

    /**
     * @return the ID of this feature.
     */
    public @NotNull String getId() {
        return this.id;
    }

    /**
     * Constructs a new {@code StatePair} containing a newly instantiated
     * instance of both the internal and container state.
     *
     * @param observer an observer of the I/O device which owns this state.
     *                 This allows the state to emit events.
     * @return the state pair.
     * @throws NullPointerException if {@code observer} is {@code null};
     *                              if the returned internal state or
     *                              container state are {@code null}.
     * @throws KetillException      if the device of {@code observer}
     *                              is not of required type;
     *                              if the returned internal state or
     *                              container state are instances of
     *                              {@link IoFeature};
     *                              if the returned internal state
     *                              extends {@link ContainerState};
     *                              if the returned container state
     *                              implements {@link AutonomousState}.
     */
    /* @formatter:off */
    protected final @NotNull StatePair<Z, S>
            getState(@NotNull IoDeviceObserver observer) {
        Objects.requireNonNull(observer, "observer cannot be null");

        Class<?> deviceClazz = observer.getDevice().getClass();
        if(!deviceType.isAssignableFrom(deviceClazz)) {
            String msg = "observer.getDevice() must be of type";
            msg += " " + deviceType.getName();
            throw new KetillException(msg);
        }

        Z internalState = this.getInternalState(observer);
        Objects.requireNonNull(internalState,
                "getInternalState() cannot return null");

        S containerState = this.getContainerState(internalState);
        Objects.requireNonNull(containerState,
                "getContainerState() cannot return null");

        if (internalState instanceof IoFeature) {
            String msg = "internal state cannot be an";
            msg += " " + this.getClass().getSimpleName();
            throw new KetillException(msg);
        } else if (containerState instanceof IoFeature) {
            String msg = "container state cannot be an";
            msg += " " + this.getClass().getSimpleName();
            throw new KetillException(msg);
        }

        if (containerState instanceof AutonomousState) {
            String msg = "container state cannot be autonomous";
            msg += " (cannot implement ";
            msg += AutonomousState.class.getSimpleName() + ")";
            throw new KetillException(msg);
        } else if (internalState instanceof ContainerState) {
            String msg = "internal state cannot be a container";
            msg += " (cannot extend ";
            msg += ContainerState.class.getSimpleName() + ")";
            throw new KetillException(msg);
        }

        return new StatePair<>(internalState, containerState);
    }
    /* @formatter:on */

    /**
     * <b>Note:</b> This cannot be an {@link IoFeature} instance. This is
     * to prevent possible headaches with other methods.
     *
     * @param observer an observer of the I/O device which owns this state.
     *                 This can be used to emit events if desired.
     * @return a newly instantiated instance of the internal state. This
     * method must <i>never</i> return {@code null}.
     */
    /* @formatter:off */
    protected abstract @NotNull Z
            getInternalState(@NotNull IoDeviceObserver observer);
    /* @formatter:on */

    /**
     * <b>Note:</b> This cannot be an {@link IoFeature} instance. This is
     * to prevent possible headaches with other methods.
     *
     * @param internalState the internal state. This can be used to access
     *                      sensitive data without while limiting the user
     *                      as necessary (e.g., by making it read only).
     * @return a newly instantiated instance of the container state. This
     * method must <i>never</i> return {@code null}.
     */
    protected abstract @NotNull S getContainerState(@NotNull Z internalState);

}
