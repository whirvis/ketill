package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * An {@link IoFeature} which has an internal state identical to the
 * container state. This is useful for features which store no data
 * that needs to be hidden from the user.
 * <p>
 * <b>Requirements:</b> The state in question <i>cannot</i> implement
 * {@link AutonomousState} or extend {@link ContainerState} as it is
 * both the internal state and container state.
 *
 * @param <S> the state container type.
 * @see IoDevice#registerFeature(IoFeature)
 * @see FeaturePresent
 * @see FeatureState
 */
public class PlainIoFeature<S> extends IoFeature<S, S> {

    private final @NotNull InitialStateSupplier<@NotNull S> supplier;

    /**
     * Constructs a new {@code PlainIoFeature}.
     *
     * @param deviceType the type an {@link IoDevice} must be for it to
     *                   create an instance of this feature's state.
     * @param id         the feature ID.
     * @param supplier   a supplier for the feature's initial state.
     * @throws NullPointerException     if {@code requiredType}, {@code id}
     *                                  or {@code supplier} are {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public PlainIoFeature(@NotNull Class<? extends IoDevice> deviceType,
                          @NotNull String id,
                          @NotNull InitialStateSupplier<@NotNull S> supplier) {
        super(deviceType, id);
        this.supplier = Objects.requireNonNull(supplier,
                "supplier cannot be null");
    }

    /**
     * Constructs a new {@code PlainIoFeature} which can be registered by
     * any type of {@link IoDevice}.
     *
     * @param id       the feature ID.
     * @param supplier a supplier for the feature's initial state.
     * @throws NullPointerException     if {@code id} or {@code supplier}
     *                                  are {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public PlainIoFeature(@NotNull String id,
                          @NotNull InitialStateSupplier<@NotNull S> supplier) {
        this(IoDevice.class, id, supplier);
    }

    /**
     * Constructs a new {@code PlainIoFeature}.
     *
     * @param deviceType the type an {@link IoDevice} must be for it to
     *                   create an instance of this feature's state.
     * @param id         the feature ID.
     * @param supplier   a supplier for the feature's initial state.
     * @throws NullPointerException     if {@code requiredType}, {@code id}
     *                                  or {@code supplier} are {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public PlainIoFeature(@NotNull Class<? extends IoDevice> deviceType,
                          @NotNull String id, Supplier<@NotNull S> supplier) {
        this(deviceType, id, InitialStateSupplier.wrap(supplier));
    }

    /**
     * Constructs a new {@code PlainIoFeature} which can be registered by
     * any type of {@link IoDevice}.
     *
     * @param id       the feature ID.
     * @param supplier a supplier for the feature's initial state.
     * @throws NullPointerException     if {@code id} or {@code supplier}
     *                                  are {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public PlainIoFeature(@NotNull String id, Supplier<@NotNull S> supplier) {
        this(IoDevice.class, id, supplier);
    }

    /* @formatter:off */
    @Override
    protected final @NotNull S
            getInternalState(@NotNull IoDeviceObserver observer) {
        return supplier.get(this, observer);
    }
    /* @formatter:on */

    @Override
    protected final @NotNull S getContainerState(@NotNull S internalState) {
        return internalState;
    }

}
