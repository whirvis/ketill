package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class MappedFeature<R extends RegisteredFeature<?, Z, ?>, Z, P> {

    private static final Runnable NO_OP = () -> {
        /* nothing to update */
    };

    private final @Nullable P params;
    private final @NotNull StateUpdater<Z, P> updater;

    MappedFeature(@Nullable P params, @NotNull StateUpdater<Z, P> updater) {
        this.params = params;
        this.updater = updater;
    }

    @NotNull Runnable getUpdater(@NotNull R registered) {
        Runnable internalStateUpdater;
        if (registered.internalState instanceof LivingState) {
            LivingState life = (LivingState) registered.internalState;
            internalStateUpdater = life::update;
        } else {
            internalStateUpdater = NO_OP;
        }

        Runnable containerStateUpdater;
        if (registered.containerState instanceof LivingState) {
            LivingState life = (LivingState) registered.containerState;
            containerStateUpdater = life::update;
        } else {
            containerStateUpdater = NO_OP;
        }

        /*
         * The order for updating is decided in a specific way. The state
         * updater is run first, as it updates adapter specific data. The
         * internal state updater is run second, as it is what's referenced
         * by the container state for public facing data. The container is
         * run last as it depends on the data from the internal state.
         */
        return () -> {
            updater.update(registered.internalState, params);
            internalStateUpdater.run();
            containerStateUpdater.run();
        };
    }

}
