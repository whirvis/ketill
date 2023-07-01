package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * An {@link IoFeature} which can be implemented via functional interfaces
 * inspired by Java's {@code Supplier} interface.
 *
 * @param <S> the I/O state type.
 * @see StateSupplier
 * @see IoDevice#addFeature(IoFeature)
 */
public class SuppliedIoFeature<S extends IoState<?>> extends IoFeature<S> {

    /**
     * Represents a supplier for the state of a {@link SuppliedIoFeature}.
     *
     * @param <S> the I/O state type.
     */
    @FunctionalInterface
    public interface StateSupplier<S extends IoState<?>> {

        /**
         * Creates a new instance of the I/O feature's state.
         *
         * @param device  the I/O device which the state will belong to.
         * @param feature the I/O feature which the state will represent.
         * @return the newly created I/O state.
         * @see IoFeature#createState(IoDevice)
         */
        @NotNull S get(@NotNull IoDevice device,
                       @NotNull IoFeature<S> feature);

    }

    private final @NotNull StateSupplier<S> stateSupplier;

    /**
     * Constructs a new {@code PlainIoFeature}.
     *
     * @param id            the ID of this I/O feature.
     * @param flow          the flow of this I/O feature.
     * @param stateSupplier the I/O state supplier.
     * @throws NullPointerException     if {@code id}, {@code flow},
     *                                  or {@code stateSupplier} are
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public SuppliedIoFeature(@NotNull String id, @NotNull IoFlow flow,
                             @NotNull StateSupplier<S> stateSupplier) {
        super(id, flow);
        this.stateSupplier = stateSupplier;
    }

    @Override
    protected final @NotNull S createState(@NotNull IoDevice device) {
        return stateSupplier.get(device, this);
    }

}