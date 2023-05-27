package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;

/**
 * A key assigned to a value.
 *
 * @param <T> the value type assigned to this key.
 */
@SuppressWarnings("unused")
public final class IoKey<T> {

    private static final AtomicLong ID_COUNTER =
            new AtomicLong(Long.MIN_VALUE);
    private static final LongUnaryOperator ID_INCREMENT =
            op -> op == Long.MAX_VALUE ? Long.MIN_VALUE : op + 1;

    private final long id;
    private final @NotNull Supplier<T> fallback;

    /**
     * Constructs a new {@code IoKey}.
     * <p>
     * <b>Note:</b> Although {@code fallback} cannot be {@code null}, it
     * can supply a value of {@code null} for {@link #getFallback()}.
     *
     * @param fallback a supplier for a fallback value to use in the event
     *                 no value has been assigned to the key.
     * @throws NullPointerException if {@code fallback} is {@code null}.
     */
    public IoKey(@NotNull Supplier<T> fallback) {
        this.id = ID_COUNTER.updateAndGet(ID_INCREMENT);
        this.fallback = Objects.requireNonNull(fallback,
                "fallback cannot be null");
    }

    /**
     * Constructs a new {@code IoKey}.
     *
     * @param fallback the fallback value to use if no value
     *                 has been assigned to this key.
     */
    public IoKey(@Nullable T fallback) {
        this(() -> fallback);
    }

    /**
     * Constructs a new {@code IoKey} with no fallback value.
     */
    public IoKey() {
        this(() -> null);
    }

    /**
     * Returns the fallback value of this key, if any.
     *
     * @return the fallback value of this key, {@code null}
     * if this key has no fallback value.
     */
    public @Nullable T getFallback() {
        return fallback.get();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /* do not implement equals() for this class */

    @Override
    public String toString() {
        return IoApi.getStrJoiner(this)
                .add("id=" + id + "'")
                .toString();
    }

}
