package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * A supplier of the initial state of an {@link IoFeature}, used by
 * {@link PlainIoFeature} to instantiate new states by itself.
 *
 * @param <S> the state container type.
 */
@FunctionalInterface
public interface InitialStateSupplier<S> {

    /**
     * Wraps an instance of Java's built in {@link Supplier} into an
     * instance of {@link InitialStateSupplier}.
     *
     * @param supplier the supplier to wrap.
     * @param <S>      the state container type.
     * @return the wrapped supplier, {@code null} if {@code supplier}
     * is {@code null}.
     */
    static <S> InitialStateSupplier<S> wrap(Supplier<S> supplier) {
        if (supplier == null) {
            return null;
        }
        return (feature, observer) -> supplier.get();
    }

    /**
     * Gets a newly created state.
     *
     * @param feature  the feature creating this state.
     * @param observer an observer of the I/O device which owns this state.
     *                 This can be used to emit events if desired.
     * @return the container state. This method must <i>never</i> return
     * {@code null}.
     */
    @NotNull S get(@NotNull IoFeature<?, ?> feature,
                   @NotNull IoDeviceObserver observer);

}
