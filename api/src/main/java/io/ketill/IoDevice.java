package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class IoDevice {

    /* @formatter:off */
    final IoDeviceInternals internals;
    /* @formatter:on */

    /**
     * Constructs a new {@code IoDevice}.
     *
     * @param typeId the type ID of this I/O device.
     * @throws NullPointerException     if {@code typeId} is {@code null}.
     * @throws IllegalArgumentException if {@code typeId} is empty or contains
     *                                  whitespace.
     */
    public IoDevice(@NotNull String typeId) {
        this.internals = new IoDeviceInternals(typeId);
    }

    protected IoDevice(@Nullable IoDeviceInternals internals) {
        this.internals = Objects.requireNonNull(internals,
                "internals cannot be null");
    }

    /**
     * Returns the type ID of this I/O device.
     *
     * @return the type ID of this I/O device.
     */
    public final @NotNull String getTypeId() {
        return internals.getTypeId();
    }

    /**
     * Returns if an I/O feature is present on this device.
     *
     * @param feature the feature to check for.
     * @return {@code true} if {@code feature}, is present on this device,
     * {@code false} otherwise.
     * @see #hasFeature(String)
     */
    public final boolean hasFeature(@Nullable IoFeature<?, ?> feature) {
        return internals.getFeatureCache(feature) != null;
    }

    /**
     * Returns if an I/O feature with the given ID is present on this device.
     *
     * @param id the feature ID, case-sensitive.
     * @return {@code true} if a feature with the given ID is present on this
     * device, {@code false} otherwise.
     * @see #hasFeature(IoFeature)
     */
    public final boolean hasFeature(@Nullable String id) {
        return internals.getFeatureCache(id) != null;
    }

    /**
     * Returns an I/O feature by its ID.
     *
     * @param id the feature ID, case-sensitive.
     * @return the feature with the given ID or {@code null} if no such
     * feature is present on this device.
     */
    public final @Nullable IoFeature<?, ?> getFeature(@Nullable String id) {
        IoFeature.Cache cache = internals.getFeatureCache(id);
        return cache != null ? cache.feature : null;
    }

    /**
     * Returns the number of I/O features present on this device.
     *
     * @return the number of I/O features present on this device.
     * @see #getFeatures(boolean)
     */
    public final int getFeatureCount() {
        return internals.getFeatureCount();
    }

    /**
     * Returns all I/O features present on this device.
     *
     * @param snapshot If {@code true}, this method will create a new list
     *                 containing all features present at the time of calling
     *                 this method. If {@code false}, it will return an
     *                 always up-to-date, <i>unmodifiable</i> direct view.
     * @return all I/O features present on this device.
     * @see #getFeatureCount()
     * @see #getStates(boolean)
     */
    public final @NotNull List<@NotNull IoFeature<?, ?>>
    getFeatures(boolean snapshot) {
        return internals.getFeatures(snapshot);
    }

    /**
     * Returns the current state of an I/O feature.
     * <p>
     * As guaranteed by {@link #addFeature(IoFeature)}, the returned state
     * is valid for the lifetime of the device. It will only become invalid
     * once the device is disconnected. When this occurs, the state shall
     * revert to its initial value.
     *
     * @param feature the feature to query.
     * @param <I>     the internal data type.
     * @param <S>     the I/O state type.
     * @param <M>     the mutable I/O state type.
     * @return the state of {@code feature} or {@code null} if {@code feature}
     * is not present on this device.
     */
    @SuppressWarnings("unchecked") /* enforced via generics */
    public final <I, S extends IoState<I>, M extends S>
    @Nullable S getState(@Nullable IoFeature<S, M> feature) {
        IoFeature.Cache cache = internals.getFeatureCache(feature);
        return cache != null ? (S) cache.state : null;
    }

    /**
     * Returns the current state of an I/O feature.
     * <p>
     * As guaranteed by {@link #addFeature(IoFeature)}, the returned state
     * is valid for the lifetime of the device. It will only become invalid
     * once the device is disconnected. When this occurs, the state shall
     * revert to its initial value.
     * <p>
     * <b>Note:</b> This method will return {@code null} if the feature with
     * the given ID (assuming it is present on this device) is not an instance
     * of the given type. No exception will be thrown.
     *
     * @param id   the ID of the feature to query.
     * @param type the expected I/O feature type.
     * @param <I>  the internal data type.
     * @param <S>  the I/O state type.
     * @param <M>  the mutable I/O state type.
     * @param <F>  the I/O feature type.
     * @return the state of the feature with the given ID or {@code null} if
     * no such feature is present on this device.
     * @throws NullPointerException if {@code type} is {@code null}.
     */
    @SuppressWarnings("unchecked") /* enforced via generics */
    public final <I, S extends IoState<I>, M extends S, F extends IoFeature<S, M>>
    @Nullable S getState(@Nullable String id, @NotNull Class<F> type) {
        Objects.requireNonNull(type, "type cannot be null");
        IoFeature.Cache cache = internals.getFeatureCache(id);
        if (cache == null) {
            return null;
        }
        IoFeature<?, ?> feature = cache.feature;
        if (!type.isAssignableFrom(feature.getClass())) {
            return null; /* unexpected type */
        }
        return (S) cache.state;
    }

    /**
     * Returns the current state of an I/O feature.
     * <p>
     * As guaranteed by {@link #addFeature(IoFeature)}, the returned state
     * is valid for the lifetime of the device. It will only become invalid
     * once the device is disconnected. When this occurs, the state shall
     * revert to its initial value.
     *
     * @param id the ID of the feature to query, case-sensitive.
     * @return the state of the feature with the given ID or {@code null} if
     * no such feature is present on this device.
     */
    public final @Nullable IoState<?> getState(@Nullable String id) {
        IoFeature.Cache cache = internals.getFeatureCache(id);
        return cache != null ? cache.state : null;
    }

    /**
     * Returns the states of all I/O features present on this device.
     *
     * @param snapshot If {@code true}, this method will create a new map
     *                 containing the states of all features present at the
     *                 time of calling this method. If {@code false}, it
     *                 will return an always up-to-date, <i>unmodifiable</i>
     *                 direct view.
     * @return the states all I/O features present on this device.
     * @see #getFeatureCount()
     * @see #getStates(boolean)
     */
    public final @NotNull Map<@NotNull IoFeature<?, ?>, @NotNull IoState<?>>
    getStates(boolean snapshot) {
        return internals.getStates(snapshot);
    }

    /**
     * Returns the current state of an I/O feature.
     * <p>
     * This is identical to {@link #getState(IoFeature)} except that it
     * returns the mutable state instead. It is {@code protected} so only
     * the device itself can obtain write access for a state.
     *
     * @param feature the feature to query.
     * @param <I>     the internal data type.
     * @param <S>     the I/O state type.
     * @param <M>     the mutable I/O state type.
     * @return the state of {@code feature} or {@code null} if {@code feature}
     * is not present on this device.
     */
    @SuppressWarnings("unchecked") /* enforced via generics */
    protected final <I, S extends IoState<I>, M extends S>
    @Nullable M getMutableState(@Nullable IoFeature<S, M> feature) {
        IoFeature.Cache cache = internals.getFeatureCache(feature);
        return cache != null ? (M) cache.mutable : null;
    }

    /**
     * Returns the current state of an I/O feature.
     * <p>
     * This is identical to {@link #getState(String, Class)} except that it
     * returns the mutable state instead. It is {@code protected} so only the
     * device itself can obtain write access for a state.
     *
     * @param id   the ID of the feature to query.
     * @param type the expected I/O feature type.
     * @param <I>  the internal data type.
     * @param <S>  the I/O state type.
     * @param <M>  the mutable I/O state type.
     * @param <F>  the I/O feature type.
     * @return the state of the feature with the given ID or {@code null} if
     * no such feature is present on this device.
     * @throws NullPointerException if {@code type} is {@code null}.
     */
    @SuppressWarnings("unchecked") /* enforced via generics */
    protected final <I, S extends IoState<I>, M extends S, F extends IoFeature<S, M>>
    @Nullable M getMutableState(@Nullable String id, @NotNull Class<F> type) {
        IoFeature.Cache cache = internals.getFeatureCache(id, type);
        return cache != null ? (M) cache.mutable : null;
    }

    /**
     * Returns the mutable state of an I/O feature.
     * <p>
     * This is identical to {@link #getState(String)} except that it returns
     * the mutable state instead. It is {@code protected} so only the device
     * itself can obtain write access for a state.
     *
     * @param id the ID of the feature to query, case-sensitive.
     * @return the state of the feature with the given ID or {@code null} if
     * no such feature is present on this device.
     */
    protected final @Nullable IoState<?> getMutableState(@Nullable String id) {
        IoFeature.Cache cache = internals.getFeatureCache(id);
        return cache != null ? cache.state : null;
    }

    /**
     * Returns the internal data of an I/O feature's state.
     * <p>
     * As explained by {@link #getState(IoFeature)}, the state is valid for
     * the lifetime of the device. The same is true for the state's internal
     * data. Once the state reverts to its initial value, the internal data
     * will revert as well.
     * <p>
     * <b>Note:</b> This method exists only for the benefit of subclasses.
     * The internal data of an {@link IoState} should only be accessible to
     * this device, the device's adapter, and the state itself.
     *
     * @param feature the feature to query.
     * @param <I>     the internal data type.
     * @param <S>     the I/O state type.
     * @param <M>     the mutable I/O state type.
     * @return the internal data of the state or {@code null} if
     * {@code feature} is not present on this device.
     */
    protected final <I, S extends IoState<I>, M extends S>
    @Nullable I getInternals(@NotNull IoFeature<S, M> feature) {
        S state = this.getState(feature);
        return state != null ? state.internals : null;
    }

    /**
     * Adds an I/O feature to the device.
     * <p>
     * If the given feature is already present, its current state will be
     * returned. A new instance will not be created in order to prevent a
     * dangling reference. This also ensures the state of a feature will
     * remain valid for the lifetime of the device.
     * <p>
     * <b>Note:</b> No two features with the same ID can be present on a
     * device at the same time. This ensures {@link #getFeature(String)}
     * will always return a single unambiguous value.
     *
     * @param feature the feature to add.
     * @param <S>     the I/O state type.
     * @param <I>     the internal data type.
     * @return the current state of {@code feature}, this is guaranteed to
     * remain valid for the lifetime of the device.
     * @throws NullPointerException if {@code feature} is {@code null}.
     * @throws IoDeviceException    if a feature with the same ID as
     *                              {@code feature} is already present
     *                              on this device.
     */
    protected <I, S extends IoState<I>, M extends S>
    @NotNull S addFeature(@NotNull IoFeature<S, M> feature) {
        return internals.addFeature(this, feature);
    }

}