package io.ketill;

import org.jetbrains.annotations.Nullable;

/**
 * Signals an {@link IoDevice} exception.
 */
@SuppressWarnings("unused")
public class IoDeviceException extends KetillIoException {

    private static final long serialVersionUID = 2296743015689493731L;

    private final @Nullable IoDevice culprit;

    /**
     * Constructs a new {@code IoDeviceException} with the specified detail
     * message and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this exception's detail message.
     *
     * @param culprit the I/O device which caused this exception.
     *                A {@code null} value is permitted, and indicates
     *                the culprit is either irrelevant or unknown.
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link Throwable#getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link Throwable#getCause()} method). A {@code null}
     *                value is permitted, and indicates that the cause is
     *                nonexistent or unknown.
     */
    public IoDeviceException(@Nullable IoDevice culprit,
                             @Nullable String message,
                             @Nullable Throwable cause) {
        super(message, cause);
        this.culprit = culprit;
    }

    /**
     * Constructs a new {@code IoDeviceException} with the specified detail
     * message.
     *
     * @param culprit the I/O device which caused this exception.
     *                A {@code null} value is permitted, and indicates
     *                the culprit is either irrelevant or unknown.
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link Throwable#getMessage()} method).
     */
    public IoDeviceException(@Nullable IoDevice culprit,
                             @Nullable String message) {
        super(message);
        this.culprit = culprit;
    }

    /**
     * Constructs a new {@code IoDeviceException} with the specified cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this exception's detail message.
     *
     * @param culprit the I/O device which caused this exception.
     *                A {@code null} value is permitted, and indicates
     *                the culprit is either irrelevant or unknown.
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link Throwable#getCause()} method). A {@code null}
     *                value is permitted, and indicates that the cause is
     *                nonexistent or unknown.
     */
    public IoDeviceException(@Nullable IoDevice culprit,
                             @Nullable Throwable cause) {
        super(cause);
        this.culprit = culprit;
    }

    /**
     * Constructs a new {@code IoDeviceException} with no detail message.
     *
     * @param culprit the I/O device which caused this exception.
     *                A {@code null} value is permitted, and indicates
     *                the culprit is either irrelevant or unknown.
     */
    public IoDeviceException(@Nullable IoDevice culprit) {
        super();
        this.culprit = culprit;
    }

    /**
     * Constructs a new {@code IoDeviceException} with the specified detail
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
    public IoDeviceException(@Nullable String message,
                             @Nullable Throwable cause) {
        this(null, message, cause);
    }

    /**
     * Constructs a new {@code IoDeviceException} with the specified detail
     * message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link Throwable#getMessage()} method).
     */
    public IoDeviceException(@Nullable String message) {
        this(null, message);
    }

    /**
     * Constructs a new {@code IoDeviceException} with the specified cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this exception's detail message.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link Throwable#getCause()} method). A {@code null}
     *              value is permitted, and indicates that the cause is
     *              nonexistent or unknown.
     */
    public IoDeviceException(@Nullable Throwable cause) {
        this((IoDevice) null, cause);
    }

    /**
     * Constructs a new {@code IoDeviceException} with no detail message.
     */
    public IoDeviceException() {
        this((IoDevice) null);
    }

    /**
     * Returns the culprit for this exception.
     *
     * @return the culprit for this exception (which may be {@code null}).
     */
    public final @Nullable IoDevice getCulprit() {
        return this.culprit;
    }

}