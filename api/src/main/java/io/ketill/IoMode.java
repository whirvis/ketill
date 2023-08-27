package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * TODO: docs
 */
public enum IoMode {

    /**
     * TODO: docs
     */
    READ(true, false, "r"),

    /**
     * TODO: docs
     */
    WRITE(false, true, "w", "a"),

    /**
     * TODO: docs
     */
    READ_WRITE(true, true, "r+", "w+", "a+");

    private final boolean read;
    private final boolean write;
    private final @NotNull List<@NotNull String> ids;

    IoMode(boolean read, boolean write, @NotNull String @NotNull ... ids) {
        this.read = read;
        this.write = write;
        this.ids = Arrays.asList(ids);
    }

    /**
     * Returns if this I/O mode supports read operations.
     *
     * @return {@code true} if this I/O mode supports read operations,
     * {@code false} otherwise.
     * @see #supports(IoMode)
     */
    public boolean isRead() {
        return this.read;
    }

    /**
     * Returns if this I/O mode supports write operations.
     *
     * @return {@code true} if this I/O mode supports write operations,
     * {@code false} otherwise.
     * @see #supports(IoMode)
     */
    public boolean isWrite() {
        return this.write;
    }

    /**
     * Returns if this I/O mode supports the operations of another.
     *
     * @param mode the mode to compare.
     * @return {@code true} if this I/O mode supports all the operations
     * of {@code mode}, {@code false} otherwise.
     * @see #isRead()
     * @see #isWrite()
     */
    public boolean supports(@Nullable IoMode mode) {
        if (mode == null) {
            return false;
        } else if (mode == this) {
            return true;
        } else {
            return (this.isRead() || !mode.isRead())
                    && (this.isWrite() || !mode.isWrite());
        }
    }

    /**
     * Returns the IDs for this I/O mode.
     *
     * @return the IDs for this I/O mode.
     * @see #of(String)
     */
    public @Nullable String @NotNull [] ids() {
        return ids.toArray(new String[0]);
    }

    /**
     * Returns the I/O mode with the specified ID. The string must match
     * exactly an ID for one of the declared modes (extraneous whitespace
     * is not permitted).
     *
     * @param id the mode ID.
     * @return the I/O mode with the specified ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if there is no mode with the
     *                                  specified ID.
     * @see #ids()
     */
    public static @NotNull IoMode of(@NotNull String id) {
        Objects.requireNonNull(id, "id cannot be null");
        for (IoMode value : IoMode.values()) {
            if (value.ids.contains(id)) {
                return value;
            }
        }
        throw new IllegalArgumentException("No mode " + id);
    }

}