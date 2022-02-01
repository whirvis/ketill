package com.whirvis.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface for updating the state of a {@link RegisteredFeature}.
 *
 * @param <S> the state container type.
 * @param <P> the mapping parameters type.
 * @see MappedFeatureRegistry#mapFeature(DeviceFeature, Object, StateUpdater)
 */
@FunctionalInterface
public interface StateUpdater<S, P> {

    /**
     * A {@link StateUpdater} which takes in no parameters.
     *
     * @param <S> the state container type.
     * @see MappedFeatureRegistry#mapFeature(DeviceFeature, NoParams)
     */
    @FunctionalInterface
    interface NoParams<S> {

        /**
         * Called by the input device when updating {@code feature}.
         *
         * @param state the state to update.
         */
        void update(@NotNull S state);

    }

    /**
     * Called by the input device when updating {@code feature}.
     *
     * @param state the state to update.
     * @param params the mapping parameters.
     */
    void update(@NotNull S state, @Nullable P params);

}
