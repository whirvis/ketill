package io.ketill;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

abstract class EventObserver<E> implements Observer<E> {

    /**
     * The original subject this event observer wraps around. This is
     * {@code protected} so child classes have direct access to the original,
     * if they so need.
     */
    protected final @NotNull Subject<E> subject;

    EventObserver(@NotNull Subject<E> subject) {
        this.subject = subject;
    }

    /**
     * This method is unsupported.
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
     *
     * @throws UnsupportedOperationException anytime this method is invoked.
     */
    @Override
    public final void onComplete() {
        throw new UnsupportedOperationException();
    }

}
