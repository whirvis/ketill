package io.ketill;

import org.jetbrains.annotations.Nullable;

/**
 * Signals that an unexpected error has occurred in the Ketill I/O API.
 * These are usually due to a bug in the implementation, not user error.
 * <p>
 * <b>If this is ever thrown</b>: Please open an issue ticket.
 */
@SuppressWarnings("unused")
final class KetillIoBug extends KetillIoException {

    private static final long serialVersionUID = 2296743015689493731L;

    /**
     * Constructs a new {@code KetillIoBug} with the specified detail
     * message and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link Throwable#getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link Throwable#getCause()} method). A {@code null}
     *                value is permitted, and indicates that the cause is
     *                nonexistent or unknown.
     */
    public KetillIoBug(@Nullable String message,
                       @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code KetillIoBug} with the specified detail
     * message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link Throwable#getMessage()} method).
     */
    public KetillIoBug(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs a new {@code KetillIoBug} with the specified cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this exception's detail message.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link Throwable#getCause()} method). A {@code null}
     *              value is permitted, and indicates that the cause is
     *              nonexistent or unknown.
     */
    public KetillIoBug(@Nullable Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code KetillIoBug} with no detail message.
     */
    public KetillIoBug() {
        super();
    }

}
