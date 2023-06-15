package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * An {@link IoFeature} with an immutable state.
 * <p>
 * An immutable I/O feature is defined as a feature whose state has no user
 * mutable variant. The state may be changed, but not by the user. Examples
 * include (but are not limited to) a gamepad button or an analog stick.
 * <p>
 * The method {@link #createMutableState(IoState)} implements immutability
 * by simply returning the given state.
 *
 * @param <S> the I/O state type.
 * @see BuiltIn
 * @see IoDevice#addFeature(IoFeature)
 * @see IoLogic
 */
public abstract class ImmutableIoFeature<S extends IoState<?>>
        extends IoFeature<S, S> {

    /**
     * Constructs a new {@code ImmutableIoFeature}.
     *
     * @param id   the ID of this I/O feature.
     * @param flow the flow of this I/O feature.
     * @throws NullPointerException     if {@code id} or {@code flow} are
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public ImmutableIoFeature(@NotNull String id, @NotNull IoFlow flow) {
        super(id, flow);
    }

    @Override
    protected final @NotNull S createMutableState(@NotNull S state) {
        return state;
    }

}