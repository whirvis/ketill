package io.ketill;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A thread-safe wrapper for {@link Subject}.
 * <p>
 * <b>Visibility:</b> This class is {@code package-private} as it exists
 * solely to ensure classes like {@link IoDeviceObserver} only implement
 * the methods necessary for event emission to function in a thread-safe
 * manner.
 * <p>
 * <b>Thread safety:</b> This class is <i>thread-safe.</i>
 *
 * @param <E> the emitter type.
 */
abstract class EventObserver<E> implements Observer<E> {

    /**
     * The original subject this event observer wraps around.
     * This is {@code protected} so child classes have direct
     * access to the original, if they so need.
     * <p>
     * <b>Thread safety:</b> This subject was serialized at
     * construction, making it <i>thread-safe.</i>
     */
    protected final @NotNull Subject<E> subject;

    EventObserver(@NotNull Subject<E> subject) {
        this.subject = subject.toSerialized();
    }

    /**
     * This method is unsupported.
     * <p>
     * <b>Thread safety:</b> Not applicable.
     *
     * @param disposable the {@link Disposable} instance whose
     *                   {@link Disposable#dispose()} can be called
     *                   anytime to cancel the connection.
     * @throws NullPointerException          if {@code disposable} is
     *                                       {@code null}.
     * @throws UnsupportedOperationException anytime this method is invoked.
     */
    @Override
    public final void onSubscribe(@NotNull Disposable disposable) {
        Objects.requireNonNull(disposable, "disposable cannot be null");
        throw new UnsupportedOperationException();
    }

    /**
     * This method is unsupported.
     * <p>
     * <b>Thread safety:</b> Not applicable.
     *
     * @param cause the exception encountered.
     * @throws NullPointerException          if {@code cause} is {@code null}.
     * @throws UnsupportedOperationException anytime this method is invoked.
     */
    @Override
    public final void onError(@NotNull Throwable cause) {
        Objects.requireNonNull(cause, "cause cannot be null");
        throw new UnsupportedOperationException();
    }

    /**
     * This method is unsupported.
     * <p>
     * <b>Thread safety:</b> Not applicable.
     *
     * @throws UnsupportedOperationException anytime this method is invoked.
     */
    @Override
    public final void onComplete() {
        throw new UnsupportedOperationException();
    }

}
