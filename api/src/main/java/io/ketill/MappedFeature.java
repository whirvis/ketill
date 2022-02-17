package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class MappedFeature<R extends RegisteredFeature<?, S>, S, P> {

    private final @Nullable P params;
    private final @NotNull StateUpdater<S, P> updater;

    MappedFeature(@Nullable P params, @NotNull StateUpdater<S, P> updater) {
        this.params = params;
        this.updater = updater;
    }

    @NotNull Runnable getUpdater(@NotNull R registered) {
        return () -> updater.update(registered.state, params);
    }

}
