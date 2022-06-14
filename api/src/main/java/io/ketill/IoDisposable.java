package io.ketill;

import io.reactivex.rxjava3.disposables.Disposable;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper for a {@link Disposable} from RxJava 3.
 * <p>
 * This class exists so users do not have to add the RxJava 3
 * library to dispose listeners.
 */
public final class IoDisposable implements Disposable {

    private final Disposable disposable;

    IoDisposable(@NotNull Disposable disposable) {
        this.disposable = disposable;
    }

    @Override
    public void dispose() {
        disposable.dispose();
    }

    @Override
    public boolean isDisposed() {
        return disposable.isDisposed();
    }

}
