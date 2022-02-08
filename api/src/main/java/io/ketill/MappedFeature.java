package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link IoFeature} that has been designated a mapping in a
 * {@link MappedFeatureRegistry}.
 *
 * @param <R> the registered feature type.
 * @param <S> the state container type.
 * @param <P> the mapping parameters type.
 */
public class MappedFeature<R extends RegisteredFeature<?, S>, S, P> {

    private final @Nullable P params;
    private final @NotNull StateUpdater<S, P> updater;

    protected MappedFeature(@Nullable P params,
                            @NotNull StateUpdater<S, P> updater) {
        this.params = params;
        this.updater = updater;
    }

    /**
     * @param registered the registered feature.
     * @return a runnable which, when run, will update the state of
     * {@code registered}.
     */
    public @NotNull Runnable getUpdater(@NotNull R registered) {
        return () -> updater.update(registered.state, params);
    }

}
