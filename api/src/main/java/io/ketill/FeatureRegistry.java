package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * This interface is package private as it exists only to ensure that both
 * {@link IoDevice} and {@link MappedFeatureRegistry} implement the same
 * methods.
 */
interface FeatureRegistry {

    /**
     * Returns if a feature is registered.
     *
     * @param feature the feature whose registration to check.
     * @return {@code true} if {@code feature} is registered, {@code false}
     * otherwise.
     * @throws NullPointerException if {@code feature} is {@code null}.
     */
    boolean isFeatureRegistered(@NotNull IoFeature<?, ?> feature);

    /**
     * Returns if a feature with a given ID is registered.
     *
     * @param id the ID of the feature to check, case-sensitive.
     * @return {@code true} if a feature with the specified ID is registered,
     * {@code false} otherwise.
     * @throws NullPointerException if {@code id} is {@code null}.
     * @see #registerFeature(IoFeature)
     */
    boolean isFeatureWithIdRegistered(@NotNull String id);

    /**
     * Returns the registered feature count.
     *
     * @return the amount of registered features.
     * @see #getFeatures()
     */
    int getFeatureCount();

    /**
     * Returns a feature with the given ID.
     *
     * @param id the ID of the feature to fetch, case-sensitive.
     * @return the registered feature with the specified ID, {@code null} if
     * no such feature is registered.
     * @see #registerFeature(IoFeature)
     */
    @Nullable IoFeature<?, ?> getFeatureById(@NotNull String id);

    /**
     * Returns all registered features.
     *
     * @return all registered features.
     * @see #getFeatureCount()
     * @see #getFeatureRegistrations()
     */
    @NotNull Collection<@NotNull IoFeature<?, ?>> getFeatures();

    /**
     * Returns the registration of a feature.
     *
     * @param feature the feature whose registration to fetch.
     * @param <Z>     the internal state type.
     * @param <S>     the state container type.
     * @return the feature registration, {@code null} if not registered.
     * @throws NullPointerException if {@code feature} is {@code null}.
     */
    /* @formatter:off */
    <Z, S> @Nullable RegisteredIoFeature<?, Z, S>
            getFeatureRegistration(@NotNull IoFeature<Z, S> feature);
    /* @formatter:on */

    /**
     * Returns the registration of all features.
     *
     * @return the registration of all features.
     * @see #getFeatureCount()
     * @see #getFeatures()
     */
    /* @formatter:off */
    @NotNull Collection<@NotNull RegisteredIoFeature<?, ?, ?>>
            getFeatureRegistrations();
    /* @formatter:on */

    /**
     * Gets the current state of a feature.
     * <p>
     * <b>Note:</b> Unlike {@link #requestState(IoFeature)}, this method
     * will not return {@code null} if {@code feature} is not registered.
     * Instead, it will throw an {@code IllegalStateException}.
     *
     * @param feature the feature whose state to fetch.
     * @param <S>     the state container type.
     * @return the current state of {@code feature}.
     * @throws NullPointerException  if {@code feature} is {@code null}.
     * @throws IllegalStateException if {@code feature} is not registered.
     */
    default <S> @NotNull S getState(@NotNull IoFeature<?, S> feature) {
        RegisteredIoFeature<?, ?, S> registered =
                this.getFeatureRegistration(feature);
        if (registered == null) {
            String msg = "no such feature \"" + feature.getId() + "\"";
            throw new IllegalStateException(msg);
        }
        return registered.containerState;
    }

    /**
     * Gets the current state of a feature.
     * <p>
     * <b>Note:</b> Unlike {@link #getState(IoFeature)}, this method will
     * not throw an {@code IllegalStateException} if {@code feature} is not
     * registered. Instead, it will simply return {@code null}.
     *
     * @param feature the feature whose state to fetch.
     * @param <S>     the state container type.
     * @return the current state of {@code feature}, {@code null} if
     * it is not registered.
     * @throws NullPointerException if {@code feature} is {@code null}.
     */
    default <S> @Nullable S requestState(@NotNull IoFeature<?, S> feature) {
        RegisteredIoFeature<?, ?, S> registered =
                this.getFeatureRegistration(feature);
        return registered != null ? registered.containerState : null;
    }

    /**
     * Registers a feature.
     *
     * @param feature the feature to register.
     * @param <F>     the device feature type.
     * @param <Z>     the internal state type.
     * @param <S>     the state container type.
     * @return the feature registration.
     * @throws NullPointerException  if {@code feature} is
     *                               {@code null}.
     * @throws IllegalStateException if {@code feature} is already registered;
     *                               if another feature with the same ID as
     *                               {@code feature} is already registered,
     *                               regardless of casing;
     *                               if the {@code internalState} or
     *                               {@code containerState} supplied by
     *                               {@code feature} belongs to a previously
     *                               registered feature.
     */
    /* @formatter:off */
    <F extends IoFeature<Z, S>, Z, S> @NotNull RegisteredIoFeature<F, Z, S>
            registerFeature(@NotNull F feature);
    /* @formatter:on */

    /**
     * Unregisters a feature.
     *
     * @param feature the feature to unregister.
     * @throws NullPointerException  if {@code feature} is {@code null}.
     * @throws IllegalStateException if {@code feature} is not registered.
     */
    void unregisterFeature(@NotNull IoFeature<?, ?> feature);

}
