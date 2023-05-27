package io.ketill;

import org.jetbrains.annotations.Nullable;

/**
 * Signals the Ketill I/O API has entered an unexpected state, likely due to
 * a bug in the implementation and not user error.
 * <p>
 * <b>If this is ever thrown</b>: Please open an issue ticket, it is a bug.
 */
@SuppressWarnings("unused")
final class UnexpectedStateException extends KetillIoException {

    private static final long serialVersionUID = -8240735556304886022L;

    /**
     * Constructs a new {@code UnexpectedStateException} with the specified
     * detail message and cause.
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
    public UnexpectedStateException(@Nullable String message,
                                    @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code UnexpectedStateException} with the specified
     * detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link Throwable#getMessage()} method).
     */
    public UnexpectedStateException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs a new {@code UnexpectedStateException} with the specified
     * cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this exception's detail message.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link Throwable#getCause()} method). A {@code null}
     *              value is permitted, and indicates that the cause is
     *              nonexistent or unknown.
     */
    public UnexpectedStateException(@Nullable Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code UnexpectedStateException} with no detail
     * message.
     */
    public UnexpectedStateException() {
        super();
    }

}
