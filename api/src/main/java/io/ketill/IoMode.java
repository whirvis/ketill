package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * Describes an I/O mode for an {@link IoDevice}.
 * <p>
 * This determines if an I/O device can be read from, written to, or both.
 * Some devices may require a mode read and/or write operations. It is akin
 * to the {@code fopen()} function's {@code mode} parameter in C.
 *
 * @see IoAdapter#open(IoMode)
 */
public final class IoMode {

    private static final int
            READ_BIT = 0b0001, OPT_READ_BIT = READ_BIT | 0b0010,
            WRITE_BIT = 0b0100, OPT_WRITE_BIT = WRITE_BIT | 0b1000;

    private static boolean isRead(int bits) {
        return (bits & READ_BIT) == READ_BIT;
    }

    private static boolean isReadOrNop(int bits) {
        return (bits & OPT_READ_BIT) == OPT_READ_BIT;
    }

    private static boolean isWrite(int bits) {
        return (bits & WRITE_BIT) == WRITE_BIT;
    }

    private static boolean isWriteOrNop(int bits) {
        return (bits & OPT_WRITE_BIT) == OPT_WRITE_BIT;
    }

    private static int verifyBits(int bits) {
        if (bits == 0) {
            throw new IllegalArgumentException("missing bitflags");
        } else if (isReadOrNop(bits) && !isRead(bits)) {
            throw new IllegalArgumentException("read flag conflict");
        } else if (isWriteOrNop(bits) && !isWrite(bits)) {
            throw new IllegalArgumentException("write flag conflict");
        }
        return bits;
    }

    /**
     * Read-only mode (equivalent to the {@code "r"} file mode).
     * <p>
     * If a read handle cannot be opened for a target, an exception will
     * be thrown.
     *
     * @see #OPT_READ
     */
    public static final @NotNull IoMode
            READ = new IoMode(READ_BIT);

    /**
     * Write-only mode (equivalent to the {@code "w"} file mode).
     * <p>
     * If a write handle cannot be opened for a target, an exception will
     * be thrown.
     *
     * @see #OPT_WRITE
     */
    public static final @NotNull IoMode
            WRITE = new IoMode(WRITE_BIT);

    /**
     * Read-write mode (equivalent to the {@code "rw"} file mode).
     * <p>
     * If a read-write handle cannot be opened for a target, an exception
     * will be thrown.
     *
     * @see #OPT_READ_WRITE
     * @see #READ_OPT_WRITE
     */
    public static final @NotNull IoMode
            READ_WRITE = new IoMode(READ_BIT | WRITE_BIT);

    /**
     * Optional read-only mode.
     * <p>
     * If a read handle cannot be opened for a target, no exception will
     * be thrown. Instead, read operations will do nothing (and zero out
     * data as needed).
     *
     * @see #READ
     */
    public static final @NotNull IoMode
            OPT_READ = new IoMode(OPT_READ_BIT);

    /**
     * Optional write-only mode.
     * <p>
     * If a write handle cannot be opened for a target, no exception will
     * be thrown. Instead, write operations will do nothing (and zero out
     * data as needed).
     *
     * @see #WRITE
     */
    public static final @NotNull IoMode
            OPT_WRITE = new IoMode(OPT_WRITE_BIT);

    /**
     * Read-write mode with optional read.
     * <p>
     * If a read handle cannot be opened for a target, write operations will
     * do nothing (and zero out data as needed). If a write handle cannot be
     * opened for a target, an exception will be thrown.
     *
     * @see #READ_WRITE
     */
    public static final @NotNull IoMode
            OPT_READ_WRITE = new IoMode(OPT_READ_BIT | WRITE_BIT);

    /**
     * Read-write mode with optional write.
     * <p>
     * If a read handle cannot be opened for a target, an exception will
     * be thrown. If a write handle cannot be opened for a target, write
     * operations will do nothing (and zero out data as needed).
     *
     * @see #READ_WRITE
     */
    public static final @NotNull IoMode
            READ_OPT_WRITE = new IoMode(READ_BIT | OPT_WRITE_BIT);

    private final int bits;

    private final boolean read;
    private final boolean readOrNop;
    private final boolean write;
    private final boolean writeOrNop;

    private IoMode(int bits) {
        this.bits = verifyBits(bits);

        this.read = isRead(bits);
        this.readOrNop = isReadOrNop(bits);
        this.write = isWrite(bits);
        this.writeOrNop = isWriteOrNop(bits);
    }

    /**
     * Returns if this I/O mode supports read operations.
     *
     * @return {@code true} if this I/O mode supports read operations,
     * {@code false} otherwise.
     * @see #isReadOrNop()
     */
    public boolean isRead() {
        return this.read;
    }

    /**
     * Returns if this I/O mode supports no-op for read operations.
     * <p>
     * If a read handle could not be opened for a target, read operations
     * will do nothing (and zero out data as needed).
     *
     * @return {@code true} if this I/O mode supports no-op for read
     * operations, {@code false} otherwise.
     * @see #isRead()
     */
    public boolean isReadOrNop() {
        return this.readOrNop;
    }

    /**
     * Returns if this I/O mode supports write operations.
     *
     * @return {@code true} if this I/O mode supports write operations,
     * {@code false} otherwise.
     * @see #isWriteOrNop()
     */
    public boolean isWrite() {
        return this.write;
    }

    /**
     * Returns if this I/O mode supports no-op for write operations.
     * <p>
     * If a write handle could not be opened for a target, write operations
     * will do nothing (and zero out data as needed).
     *
     * @return {@code true} if this I/O mode supports no-op for read
     * operations, {@code false} otherwise.
     * @see #isWrite()
     */
    public boolean isWriteOrNop() {
        return this.writeOrNop;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IoMode)) {
            return false;
        } else if (this == obj) {
            return true;
        }
        IoMode that = (IoMode) obj;
        return this.bits == that.bits;
    }

    @Override
    public int hashCode() {
        return this.bits;
    }

    @Override
    public String toString() {
        return IoApi.getStrJoiner(this)
                .add("bits=" + bits)
                .add("read=" + read)
                .add("readOrNop=" + readOrNop)
                .add("write=" + write)
                .add("writeOrNop=" + writeOrNop)
                .toString();
    }

}