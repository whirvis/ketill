package io.ketill;

import org.jetbrains.annotations.Nullable;

/**
 * The superclass for Ketill I/O exceptions.
 * <p>
 * <b>Note:</b> This class is {@code abstract} as throwing an instance
 * of this exception is too broad.
 */
public abstract class KetillIoException extends RuntimeException {

    private static final long serialVersionUID = 2296743015689493731L;

    /**
     * Constructs a new {@code KetillIoException} with the specified detail
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
    public KetillIoException(@Nullable String message,
                             @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code KetillIoException} with the specified detail
     * message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link Throwable#getMessage()} method).
     */
    public KetillIoException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs a new {@code KetillIoException} with the specified cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this exception's detail message.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link Throwable#getCause()} method). A {@code null}
     *              value is permitted, and indicates that the cause is
     *              nonexistent or unknown.
     */
    public KetillIoException(@Nullable Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code KetillIoException} with no detail message.
     */
    public KetillIoException() {
        super();
    }
    
}
