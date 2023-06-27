package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link IoFeature} which can be implemented via functional interfaces
 * inspired by Java's {@code Supplier} interface.
 *
 * @param <S> the I/O state type.
 * @param <D> the target I/O device type.
 * @see StateSupplier
 * @see LogicSupplier
 * @see IoDevice#addFeature(IoFeature)
 */
public class SuppliedIoFeature<S extends IoState<?>, D extends IoDevice>
        extends IoFeature<S, D> {

    /**
     * Represents a supplier for the state of a {@link SuppliedIoFeature}.
     *
     * @param <S> the I/O state type.
     * @see LogicSupplier
     */
    @FunctionalInterface
    public interface StateSupplier<S extends IoState<?>, D extends IoDevice> {

        /**
         * Creates a new instance of the I/O feature's state.
         *
         * @param feature the I/O feature.
         * @return the newly created I/O state.
         * @see IoFeature#createState()
         */
        @NotNull S get(@NotNull IoFeature<S, D> feature);

    }

    /**
     * Represents a supplier for the logic of a {@link SuppliedIoFeature}.
     *
     * @param <S> the I/O state type.
     * @see StateSupplier
     */
    @FunctionalInterface
    public interface LogicSupplier<S extends IoState<?>, D extends IoDevice> {

        /**
         * Creates a new instance of the I/O feature's logic.
         *
         * @param feature the I/O feature.
         * @param device  the I/O device which {@code state} belongs to.
         * @param state   the I/O state the logic will manage.
         * @return the newly created I/O logic.
         * @see IoFeature#createLogic(IoDevice, IoState)
         */
        @NotNull IoLogic<?> get(@NotNull IoFeature<S, D> feature,
                                @NotNull D device, @NotNull S state);

    }

    private final @NotNull StateSupplier<S, D> stateSupplier;
    private final @Nullable LogicSupplier<S, D> logicSupplier;

    /**
     * Constructs a new {@code PlainIoFeature}.
     *
     * @param id            the ID of this I/O feature.
     * @param flow          the flow of this I/O feature.
     * @param deviceType    the target I/O device type's class.
     * @param stateSupplier the I/O state supplier.
     * @param logicSupplier the I/O logic supplier.
     * @throws NullPointerException     if {@code id}, {@code flow},
     *                                  {@code deviceType} or
     *                                  {@code stateSupplier} are
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public SuppliedIoFeature(@NotNull String id, @NotNull IoFlow flow,
                             @NotNull Class<D> deviceType,
                             @NotNull StateSupplier<S, D> stateSupplier,
                             @Nullable LogicSupplier<S, D> logicSupplier) {
        super(id, flow, deviceType);
        this.stateSupplier = stateSupplier;
        this.logicSupplier = logicSupplier;
    }

    /**
     * Constructs a new {@code PlainIoFeature}.
     *
     * @param id            the ID of this I/O feature.
     * @param flow          the flow of this I/O feature.
     * @param deviceType    the target I/O device type's class.
     * @param stateSupplier the I/O state supplier.
     * @throws NullPointerException     if {@code id}, {@code flow},
     *                                  {@code deviceType} or
     *                                  {@code stateSupplier} are
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public SuppliedIoFeature(@NotNull String id, @NotNull IoFlow flow,
                             @NotNull Class<D> deviceType,
                             @NotNull StateSupplier<S, D> stateSupplier) {
        this(id, flow, deviceType, stateSupplier, null);
    }

    @Override
    protected final @NotNull S createState() {
        return stateSupplier.get(this);
    }

    @Override
    protected final @Nullable IoLogic<?>
    createLogic(@NotNull D device, @NotNull S state) {
        if (logicSupplier == null) {
            return super.createLogic(device, state);
        }
        return logicSupplier.get(this, device, state);
    }

}