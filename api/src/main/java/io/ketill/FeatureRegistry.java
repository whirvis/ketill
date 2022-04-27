package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

interface FeatureRegistry {

    /**
     * @param feature the feature whose registration to check.
     * @return {@code true} if {@code feature} is registered, {@code false}
     * otherwise.
     * @throws NullPointerException if {@code feature} is {@code null}.
     */
    boolean isFeatureRegistered(@NotNull IoFeature<?, ?> feature);

    /**
     * @return the amount of registered features.
     * @see #getFeatures()
     */
    int getFeatureCount();

    /**
     * @return all registered features.
     * @see #getFeatureCount()
     */
    @NotNull Collection<@NotNull RegisteredFeature<?, ?, ?>> getFeatures();

    /**
     * @param feature the feature whose registration to fetch.
     * @param <Z>     the internal state type.
     * @param <S>     the state container type.
     * @return the feature registration, {@code null} if not registered.
     * @throws NullPointerException if {@code feature} is {@code null}.
     */
    /* @formatter:off */
    <Z, S> @Nullable RegisteredFeature<?, Z, S>
            getFeatureRegistration(@NotNull IoFeature<Z, S> feature);
    /* @formatter:on */

    /**
     * Unlike {@link #requestState(IoFeature)}, this method will not return
     * {@code null} if {@code feature} is not registered. Rather, it will
     * throw an {@code IllegalStateException}.
     *
     * @param feature the feature whose state to fetch.
     * @param <S>     the state container type.
     * @return the current state of {@code feature}.
     * @throws NullPointerException  if {@code feature} is {@code null}.
     * @throws IllegalStateException if {@code feature} is not registered.
     */
    default <S> @NotNull S getState(@NotNull IoFeature<?, S> feature) {
        RegisteredFeature<?, ?, S> registered =
                this.getFeatureRegistration(feature);
        if (registered == null) {
            String msg = "no such feature \"" + feature.id + "\"";
            throw new IllegalStateException(msg);
        }
        return registered.containerState;
    }

    /**
     * Unlike {@link #getState(IoFeature)}, this method will not throw an
     * {@code IllegalStateException} if {@code feature} is not registered.
     * Rather, it will simply return {@code null}.
     *
     * @param feature the feature whose state to fetch.
     * @param <S>     the state container type.
     * @return the current state of {@code feature}, {@code null} if
     * it is not registered.
     * @throws NullPointerException if {@code feature} is {@code null}.
     */
    default <S> @Nullable S requestState(@NotNull IoFeature<?, S> feature) {
        RegisteredFeature<?, ?, S> registered =
                this.getFeatureRegistration(feature);
        return registered != null ? registered.containerState : null;
    }

    /**
     * @param feature the feature to register.
     * @param <F>     the device feature type.
     * @param <Z>     the internal state type.
     * @param <S>     the state container type.
     * @return the feature registration.
     * @throws NullPointerException  if {@code feature} is
     *                               {@code null}.
     * @throws IllegalStateException if {@code feature} is already registered;
     *                               if the {@code internalState} or
     *                               {@code containerState} supplied by
     *                               {@code feature} belong to a previously
     *                               registered feature.
     */
    /* @formatter:off */
    <F extends IoFeature<Z, S>, Z, S> @NotNull RegisteredFeature<F, Z, S>
            registerFeature(@NotNull F feature);
    /* @formatter:on */

    /**
     * @param feature the feature to unregister.
     * @throws NullPointerException  if {@code feature} is {@code null}.
     * @throws IllegalStateException if {@code feature} is not registered.
     */
    void unregisterFeature(@NotNull IoFeature<?, ?> feature);

}
