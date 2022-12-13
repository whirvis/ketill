package io.ketill;

import org.jetbrains.annotations.Nullable;

/**
 * Signals an {@link IoState} exception.
 */
@IgnoreCoverage
public class IoStateException extends KetillIoException {

    private static final long serialVersionUID = 2296743015689493731L;

    private final @Nullable IoState<?> culprit;

    /**
     * Constructs a new {@code IoStateException} with the specified detail
     * message and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this exception's detail message.
     *
     * @param culprit the I/O state which caused this exception.
     *                A {@code null} value is permitted, and indicates
     *                the culprit is either irrelevant or unknown.
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link Throwable#getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link Throwable#getCause()} method). A {@code null}
     *                value is permitted, and indicates that the cause is
     *                nonexistent or unknown.
     */
    public IoStateException(@Nullable IoState<?> culprit,
                              @Nullable String message,
                              @Nullable Throwable cause) {
        super(message, cause);
        this.culprit = culprit;
    }

    /**
     * Constructs a new {@code IoStateException} with the specified detail
     * message.
     *
     * @param culprit the I/O state which caused this exception.
     *                A {@code null} value is permitted, and indicates
     *                the culprit is either irrelevant or unknown.
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link Throwable#getMessage()} method).
     */
    public IoStateException(@Nullable IoState<?> culprit,
                              @Nullable String message) {
        super(message);
        this.culprit = culprit;
    }

    /**
     * Constructs a new {@code IoStateException} with the specified cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this exception's detail message.
     *
     * @param culprit the I/O state which caused this exception.
     *                A {@code null} value is permitted, and indicates
     *                the culprit is either irrelevant or unknown.
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link Throwable#getCause()} method). A {@code null}
     *              value is permitted, and indicates that the cause is
     *              nonexistent or unknown.
     */
    public IoStateException(@Nullable IoState<?> culprit,
                              @Nullable Throwable cause) {
        super(cause);
        this.culprit = culprit;
    }

    /**
     * Constructs a new {@code IoStateException} with no detail message.
     *
     * @param culprit the I/O state which caused this exception.
     *                A {@code null} value is permitted, and indicates
     *                the culprit is either irrelevant or unknown.
     */
    public IoStateException(@Nullable IoState<?> culprit) {
        super();
        this.culprit = culprit;
    }

    /**
     * Constructs a new {@code IoStateException} with the specified detail
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
    public IoStateException(@Nullable String message,
                              @Nullable Throwable cause) {
        this(null, message, cause);
    }

    /**
     * Constructs a new {@code IoStateException} with the specified detail
     * message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link Throwable#getMessage()} method).
     */
    public IoStateException(@Nullable String message) {
        this(null, message);
    }

    /**
     * Constructs a new {@code IoStateException} with the specified cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this exception's detail message.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link Throwable#getCause()} method). A {@code null}
     *              value is permitted, and indicates that the cause is
     *              nonexistent or unknown.
     */
    public IoStateException(@Nullable Throwable cause) {
        this((IoState<?>) null, cause);
    }

    /**
     * Constructs a new {@code IoStateException} with no detail message.
     */
    public IoStateException() {
        this((IoState<?>) null);
    }

    /**
     * Returns the culprit for this exception.
     *
     * @return the culprit for this exception (which may be {@code null}).
     */
    public final @Nullable IoState<?> getCulprit() {
        return this.culprit;
    }

}
