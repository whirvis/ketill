package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Gives autonomy to an {@link IoFeature}.
 * <p>
 * This should be used to implement functionality which an {@link IoAdapter}
 * is not responsible for. Examples of this include (but are not limited to):
 * emitting press events, applying calibration, etc.
 *
 * @param <I> the internal data type.
 * @see IoFeature#createLogic(IoDevice, IoState)
 */
public abstract class IoLogic<I> {

    /**
     * The I/O device managing this logic.
     */
    protected final @NotNull IoDevice device;

    /**
     * The I/O feature of the state.
     */
    protected final @NotNull IoFeature<?> feature;

    /**
     * The I/O state this logic is responsible for.
     */
    protected final @NotNull IoState<?> state;

    /**
     * The internals of the I/O state.
     */
    protected final @NotNull I internals;

    /**
     * Constructs a new {@code IoLogic}.
     *
     * @param device  the I/O device managing this logic.
     * @param feature the I/O feature of {@code state}.
     * @param state   the I/O state this logic is responsible for.
     * @throws NullPointerException     if {@code device}, {@code feature},
     *                                  or {@code state} are {@code null}.
     * @throws IllegalArgumentException if {@code state} does not represent
     *                                  {@code feature}.
     */
    public IoLogic(@NotNull IoDevice device, @NotNull IoFeature<?> feature,
                   @NotNull IoState<I> state) {
        Objects.requireNonNull(device, "device cannot be null");
        Objects.requireNonNull(feature, "feature cannot be null");
        Objects.requireNonNull(state, "state cannot be null");

        /*
         * Double-check that the given feature is the one the I/O state
         * actually represents. The feature must be specified due to the
         * restrictions on generics (calling state.getFeature() results
         * in the type being lost). As such, a caller could (presumably
         * by accident) pass the wrong feature.
         */
        if (state.getFeature() != feature) {
            String msg = "state must represent feature";
            throw new IllegalArgumentException(msg);
        }

        this.device = device;
        this.feature = feature;
        this.state = state;

        this.internals = state.internals;
    }

    /**
     * Initializes this logic.
     * <p>
     * This is invoked just after the {@link IoFeature} this logic works
     * for is added to a device.
     * <p>
     * <b>Default behavior:</b> No-op.
     */
    @IoApi.DefaultBehavior("no-op")
    protected void init() {
        /* default behavior is a no-op */
    }

    /**
     * Prepares for a logical update.
     * <p>
     * This is invoked just before the {@link IoAdapter} for a device
     * updates the state this logic manages.
     * <p>
     * <b>Default behavior:</b> No-op.
     */
    @IoApi.DefaultBehavior("no-op")
    protected void preprocess(IoFlow flow) {
        /* default behavior is a no-op */
    }

    /**
     * Executes a logical update.
     * <p>
     * This is invoked just after the {@link IoAdapter} for a device
     * updates the state this logic manages.
     * <p>
     * <b>Default behavior:</b> No-op.
     */
    @IoApi.DefaultBehavior("no-op")
    protected void postprocess(IoFlow flow) {
        /* default behavior is a no-op */
    }

    /**
     * Terminates this logic.
     * <p>
     * This is invoked just after the {@link IoFeature} this logic works
     * for is removed from a device.
     * <p>
     * <b>Default behavior:</b> No-op.
     */
    @IoApi.DefaultBehavior("no-op")
    protected void terminate() {
        /* default behavior is a no-op */
    }

}