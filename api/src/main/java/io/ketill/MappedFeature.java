package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class MappedFeature<R extends RegisteredFeature<?, Z, ?>, Z, P> {

    private final @Nullable P params;
    private final @NotNull StateUpdater<Z, P> updater;

    MappedFeature(@Nullable P params, @NotNull StateUpdater<Z, P> updater) {
        this.params = params;
        this.updater = updater;
    }

    @NotNull Runnable getUpdater(@NotNull R registered) {
        return () -> updater.update(registered.internalState, params);
    }

}
