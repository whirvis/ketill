package io.ketill.xinput;

import io.ketill.KetillException;
import org.jetbrains.annotations.Nullable;

/**
 * Signals that an error relating to the X-input adapter for Ketill has
 * occurred.
 */
@SuppressWarnings("unused")
public final class XInputException extends KetillException {

    /**
     * Constructs a new {@code XInputException} with the specified detail
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
    XInputException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code XInputException} with the specified detail
     * message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link Throwable#getMessage()} method).
     */
    XInputException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs a new {@code XInputException} with the specified cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this exception's detail message.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link Throwable#getCause()} method). A {@code null}
     *              value is permitted, and indicates that the cause is
     *              nonexistent or unknown.
     */
    XInputException(@Nullable Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code XInputException} with no detail message.
     */
    XInputException() {
        super();
    }

}
