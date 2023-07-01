package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"unused", "SameParameterValue"})
public abstract class IoDevice {

    private final @NotNull String typeId;
    private final @NotNull IoDeviceFeatures features;

    /**
     * Constructs a new {@code IoDevice}.
     *
     * @param typeId the type ID of this I/O device.
     * @throws NullPointerException     if {@code typeId} is {@code null}.
     * @throws IllegalArgumentException if {@code typeId} is empty or
     *                                  contains whitespace.
     * @throws IoFeatureException       if any fields in this class annotated
     *                                  with {@link IoFeature.BuiltIn} do not
     *                                  meet its requirements.
     * @throws IoStateException         if any fields in this class annotated
     *                                  with {@link IoState.BuiltIn} do not
     *                                  meet its requirements.
     */
    public IoDevice(@NotNull String typeId) {
        this.typeId = IoApi.validateId(typeId);
        this.features = new IoDeviceFeatures(this);

        Class<? extends IoDevice> clazz = this.getClass();
        IoFeature.validateBuiltInFields(clazz);
        IoState.validateBuiltInFields(clazz);
    }

    /**
     * Returns the type ID of this I/O device.
     *
     * @return the type ID of this I/O device.
     */
    public final @NotNull String getTypeId() {
        return this.typeId;
    }

    /**
     * Returns if an I/O feature is present on this device.
     *
     * @param feature the feature to check for.
     * @return {@code true} if {@code feature}, is present on this device,
     * {@code false} otherwise.
     * @see #hasFeature(String)
     */
    public final boolean hasFeature(@Nullable IoFeature<?> feature) {
        return features.getCache(feature) != null;
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
        return features.getCache(id) != null;
    }

    /**
     * Returns an I/O feature by its ID.
     *
     * @param id the feature ID, case-sensitive.
     * @return the feature with the given ID or {@code null} if no such
     * feature is present on this device.
     */
    public final @Nullable IoFeature<?> getFeature(@Nullable String id) {
        IoFeature.Cache cache = features.getCache(id);
        return cache != null ? cache.feature : null;
    }

    /**
     * Returns an I/O feature by its ID.
     *
     * @param id   the feature ID, case-sensitive.
     * @param type the expected feature type. If a feature with the
     *             given ID is present on this device but is not of
     *             this type, {@code null} will be returned.
     * @return the feature with the given ID and of the expected type or
     * {@code null} if no such feature is present on this device.
     * @throws NullPointerException if {@code type} is {@code null}.
     */
    @SuppressWarnings("unchecked") /* it is checked */
    public final @Nullable <F extends IoFeature<?>>
    F getFeature(@Nullable String id, @NotNull Class<F> type) {
        Objects.requireNonNull(type, "type cannot be null");
        IoFeature.Cache cache = features.getCache(id);
        if (cache == null) {
            return null;
        }
        IoFeature<?> feature = cache.feature;
        if (!type.isAssignableFrom(feature.getClass())) {
            return null; /* unexpected type */
        }
        return (F) feature;
    }

    /**
     * Returns the number of I/O features present on this device.
     *
     * @return the number of I/O features present on this device.
     * @see #getFeatures()
     */
    public final int getFeatureCount() {
        return features.size();
    }

    /**
     * Returns all I/O features present on this device.
     * <p>
     * <b>Note:</b> The returned list is an <i>unmodifiable</i> direct view.
     *
     * @return all I/O features present on this device.
     * @see #getFeatureCount()
     * @see #getStates()
     */
    public final @NotNull List<@NotNull IoFeature<?>> getFeatures() {
        return features.asList();
    }

    /**
     * Returns the current state of an I/O feature.
     * <p>
     * As guaranteed by {@link #addFeature(IoFeature)}, the returned state
     * is valid for the lifetime of this device. It will only become invalid
     * once this device is disconnected. When this occurs, the state shall
     * revert to its initial value.
     *
     * @param feature the feature to query.
     * @param <I>     the internal data type.
     * @param <S>     the I/O state type.
     * @return the state of {@code feature} or {@code null} if {@code feature}
     * is not present on this device.
     */
    @SuppressWarnings("unchecked") /* enforced via generics */
    public final <I, S extends IoState<I>>
    @Nullable S getState(@Nullable IoFeature<S> feature) {
        IoFeature.Cache cache = features.getCache(feature);
        return cache != null ? (S) cache.state : null;
    }

    /**
     * Returns the current state of an I/O feature.
     * <p>
     * As guaranteed by {@link #addFeature(IoFeature)}, the returned state
     * is valid for the lifetime of this device. It will only become invalid
     * once this device is disconnected. When this occurs, the state shall
     * revert to its initial value.
     *
     * @param id the feature ID, case-sensitive.
     * @return the state of the feature with the given ID or {@code null} if
     * no such feature is present on this device.
     */
    public final @Nullable IoState<?> getState(@Nullable String id) {
        IoFeature.Cache cache = features.getCache(id);
        return cache != null ? cache.state : null;
    }

    /**
     * Returns the current state of an I/O feature.
     * <p>
     * As guaranteed by {@link #addFeature(IoFeature)}, the returned state
     * is valid for the lifetime of this device. It will only become invalid
     * once this device is disconnected. When this occurs, the state shall
     * revert to its initial value.
     *
     * @param id   the ID of the feature to query.
     * @param type the expected feature type. If a feature with the
     *             given ID is present on this device but is not of
     *             this type, {@code null} will be returned.
     * @param <I>  the internal data type.
     * @param <S>  the I/O state type.
     * @param <F>  the I/O feature type.
     * @return the state of the feature with the given ID or {@code null} if
     * no such feature is present on this device.
     * @throws NullPointerException if {@code type} is {@code null}.
     */
    @SuppressWarnings("unchecked") /* enforced via generics */
    public final <I, S extends IoState<I>, F extends IoFeature<S>>
    @Nullable S getState(@Nullable String id, @NotNull Class<F> type) {
        Objects.requireNonNull(type, "type cannot be null");
        IoFeature.Cache cache = features.getCache(id);
        if (cache == null) {
            return null;
        }
        IoFeature<?> feature = cache.feature;
        if (!type.isAssignableFrom(feature.getClass())) {
            return null; /* unexpected type */
        }
        return (S) cache.state;
    }

    /**
     * Returns the states of all I/O features present on this device.
     * <p>
     * <b>Note:</b> The returned map is an <i>unmodifiable</i> direct view.
     *
     * @return the states all I/O features present on this device.
     * @see #getFeatureCount()
     * @see #getFeatures()
     */
    public final @NotNull Map<@NotNull IoFeature<?>, @NotNull IoState<?>>
    getStates() {
        return features.asMap();
    }

    /**
     * Returns the internal data of an I/O feature's state.
     * <p>
     * As explained by {@link #getState(IoFeature)}, the state is valid for
     * the lifetime of this device. The same is true for the state's internal
     * data. Once the state reverts to its initial value, the internal data
     * will revert as well.
     * <p>
     * <b>Note:</b> This method exists only for the benefit of subclasses.
     * The internal data of an {@link IoState} should only be accessible to
     * this device, this device's adapter, and the state itself.
     *
     * @param feature the feature to query.
     * @param <I>     the internal data type.
     * @param <S>     the I/O state type.
     * @return the internal data of the state or {@code null} if
     * {@code feature} is not present on this device.
     */
    protected final <I, S extends IoState<I>>
    @Nullable I getInternals(@NotNull IoFeature<S> feature) {
        S state = this.getState(feature);
        return state != null ? state.internals : null;
    }

    /**
     * Adds an I/O feature to this device.
     * <p>
     * If the given feature is already present, its current state will be
     * returned. A new instance will not be created in order to prevent a
     * dangling reference. This also ensures the state of a feature will
     * remain valid for the lifetime of this device.
     * <p>
     * <b>Note:</b> No two features with the same ID can be present on a
     * device at the same time. This ensures {@link #getFeature(String)}
     * will always return a single unambiguous value.
     *
     * @param feature the feature to add.
     * @param <S>     the I/O state type.
     * @param <I>     the internal data type.
     * @return the current state of {@code feature}, this is guaranteed to
     * remain valid for the lifetime of this device.
     * @throws NullPointerException if {@code feature} is {@code null}.
     * @throws IoDeviceException    if a feature with the same ID as
     *                              {@code feature} is already present
     *                              on this device.
     * @throws IoFeatureException   if this device is not of the feature's
     *                              required type.
     */
    protected <I, S extends IoState<I>>
    @NotNull S addFeature(@NotNull IoFeature<S, ?> feature) {
        return features.add(feature);
    }

}