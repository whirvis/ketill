package io.ketill;

import org.jetbrains.annotations.Nullable;

/**
 * Signals an {@link IoFeature} exception.
 */
public class IoFeatureException extends KetillIoException {

    private static final long serialVersionUID = 2296743015689493731L;

    /**
     * Constructs a new {@code IoFeatureException} with the specified detail
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
    public IoFeatureException(@Nullable String message,
                              @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code IoFeatureException} with the specified detail
     * message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link Throwable#getMessage()} method).
     */
    public IoFeatureException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs a new {@code IoFeatureException} with the specified cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this exception's detail message.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link Throwable#getCause()} method). A {@code null}
     *              value is permitted, and indicates that the cause is
     *              nonexistent or unknown.
     */
    public IoFeatureException(@Nullable Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code IoFeatureException} with no detail message.
     */
    public IoFeatureException() {
        super();
    }

}
