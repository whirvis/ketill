package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link IoFeature} which can be implemented via functional interfaces
 * inspired by Java's {@code Supplier} interface.
 *
 * @param <S> the I/O state type.
 * @see StateSupplier
 * @see LogicSupplier
 * @see IoDevice#addFeature(IoFeature)
 */
public class SuppliedIoFeature<S extends IoState<?>> extends IoFeature<S> {

    /**
     * Represents a supplier for the state of a {@link SuppliedIoFeature}.
     *
     * @param <S> the I/O state type.
     * @see LogicSupplier
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

    /**
     * Represents a supplier for the logic of a {@link SuppliedIoFeature}.
     *
     * @param <S> the I/O state type.
     * @see StateSupplier
     */
    @FunctionalInterface
    public interface LogicSupplier<S extends IoState<?>> {

        /**
         * Creates a new instance of the I/O feature's logic.
         *
         * @param device  the I/O device which {@code state} belongs to.
         * @param feature the I/O feature which {@code state} represents.
         * @param state   the I/O state the logic will manage.
         * @return the newly created I/O logic.
         * @see IoFeature#createLogic(IoDevice, IoState)
         */
        @NotNull IoLogic<?> get(@NotNull IoDevice device,
                                @NotNull IoFeature<S> feature,
                                @NotNull S state);

    }

    private final @NotNull StateSupplier<S> stateSupplier;
    private final @Nullable LogicSupplier<S> logicSupplier;

    /**
     * Constructs a new {@code PlainIoFeature}.
     *
     * @param id            the ID of this I/O feature.
     * @param flow          the flow of this I/O feature.
     * @param stateSupplier the I/O state supplier.
     * @param logicSupplier the I/O logic supplier.
     * @throws NullPointerException     if {@code id}, {@code flow},
     *                                  or {@code stateSupplier} are
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public SuppliedIoFeature(@NotNull String id, @NotNull IoFlow flow,
                             @NotNull StateSupplier<S> stateSupplier,
                             @Nullable LogicSupplier<S> logicSupplier) {
        super(id, flow);
        this.stateSupplier = stateSupplier;
        this.logicSupplier = logicSupplier;
    }

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
        this(id, flow, stateSupplier, null);
    }

    @Override
    protected final @NotNull S createState(@NotNull IoDevice device) {
        return stateSupplier.get(device, this);
    }

    @Override
    protected final @Nullable IoLogic<?>
    createLogic(@NotNull IoDevice device, @NotNull S state) {
        if (logicSupplier == null) {
            return super.createLogic(device, state);
        }
        return logicSupplier.get(device, this, state);
    }

}