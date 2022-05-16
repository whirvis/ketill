package io.ketill;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import org.jetbrains.annotations.NotNull;

class MockEventObserver extends EventObserver<String> {

    MockEventObserver(@NotNull Observer<String> subject) {
        super(subject);
    }

    @Override
    public void onNext(@NonNull String str) {
        /* nothing to do */
    }

}
