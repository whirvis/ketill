package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlainIoFeature<S extends IoState<?>> extends IoFeature<S> {

    @FunctionalInterface
    public interface StateSupplier<S extends IoState<?>> {
        @NotNull S supply(@NotNull IoFeature<S> feature);
    }

    @FunctionalInterface
    public interface LogicSupplier<S extends IoState<?>> {
        @NotNull IoLogic<?> supply(@NotNull IoDevice device,
                                   @NotNull S state);
    }

    private StateSupplier<S> stateSupplier;
    private LogicSupplier<S> logicSupplier;

    /**
     * Constructs a new {@code PlainIoFeature}.
     *
     * @param id            the ID of this I/O feature.
     * @param flow          the flow of this I/O feature.
     * @param stateSupplier TODO
     * @param logicSupplier TODO
     * @throws NullPointerException     if {@code id} or {@code flow} are
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public PlainIoFeature(@NotNull String id, @NotNull IoFlow flow,
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
     * @param stateSupplier TODO
     * @throws NullPointerException     if {@code id} or {@code flow} are
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public PlainIoFeature(@NotNull String id, @NotNull IoFlow flow,
                          @NotNull StateSupplier<S> stateSupplier) {
        this(id, flow, stateSupplier, null);
    }

    @Override
    protected final @NotNull S createState() {
        return stateSupplier.supply(this);
    }

    @Override
    protected final @Nullable IoLogic<?>
    createLogic(@NotNull IoDevice device, @NotNull S state) {
        if (logicSupplier == null) {
            return super.createLogic(device, state);
        }
        return logicSupplier.supply(device, state);
    }

}