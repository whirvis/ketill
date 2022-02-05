package com.whirvis.ketill;

import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A device which can send and receive input data.
 * <p/>
 * Examples of input devices include, but are not limited to: keyboards, mice,
 * XBOX controllers, etc. By design, an input device supports no features by
 * default. Rather, an extending class must provide them. The responsibility
 * of providing support for a feature is designed to the device adapter.
 * <p/>
 * <b>Note:</b> For input data to stay up-to-date, the input device must be
 * polled periodically via the {@link #poll()} method. It is recommended to
 * poll the device once every application update.
 *
 * @see DeviceSeeker
 * @see DeviceAdapter
 * @see DeviceFeature
 * @see FeaturePresent
 */
public abstract class InputDevice implements FeatureRegistry {

    public final @NotNull String id;
    private final MappedFeatureRegistry registry;
    private final DeviceAdapter<InputDevice> adapter;
    private boolean initializedAdapter;
    private boolean registeredFields;
    private boolean connected;

    private @Nullable Consumer<DeviceFeature<?>> registerFeatureCallback;
    private @Nullable Consumer<DeviceFeature<?>> unregisterFeatureCallback;
    private @Nullable Runnable connectCallback;
    private @Nullable Runnable disconnectCallback;

    /**
     * @param id              the device ID.
     * @param adapterSupplier the device adapter supplier.
     * @param registerFields  {@code true} if the constructor should call
     *                        {@link #registerFields()}. If {@code false},
     *                        the extending class must call it if it desires
     *                        the functionality of {@link FeaturePresent}.
     * @param initAdapter     {@code true} if the constructor should call
     *                        {@link #initAdapter()}. If {@code false}, the
     *                        extending class <b>must</b> call it.
     * @throws NullPointerException     if {@code id} or
     *                                  {@code adapterSupplier}
     *                                  are {@code null}; if the adapter
     *                                  given by {@code adapterSupplier}
     *                                  is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    @SuppressWarnings("unchecked")
    public InputDevice(@NotNull String id,
                       @NotNull AdapterSupplier<?> adapterSupplier,
                       boolean registerFields, boolean initAdapter) {
        this.id = Objects.requireNonNull(id, "id");
        if (id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be empty");
        } else if (!id.matches("\\S+")) {
            throw new IllegalArgumentException("id cannot contain whitespace");
        }

        this.registry = new MappedFeatureRegistry();

        /*
         * While this is an unchecked cast, the template requires that the
         * type extend InputDevice. As such, this cast is safe to perform.
         */
        Objects.requireNonNull(adapterSupplier, "adapterSupplier");
        AdapterSupplier<InputDevice> castedSupplier =
                (AdapterSupplier<InputDevice>) adapterSupplier;
        this.adapter = castedSupplier.get(this, registry);
        Objects.requireNonNull(adapter, "supplied adapter is null");

        if (registerFields) {
            this.registerFields();
        }

        if (initAdapter) {
            this.initAdapter();
        }
    }

    /**
     * This is a shorthand for the base constructor with the argument for
     * {@code registerFields} and {@code initAdapter} being {@code true}.
     *
     * @param id              the device ID.
     * @param adapterSupplier the device adapter supplier.
     * @throws NullPointerException     if {@code id} or
     *                                  {@code adapterSupplier}
     *                                  are {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public InputDevice(@NotNull String id,
                       @NotNull AdapterSupplier<?> adapterSupplier) {
        this(id, adapterSupplier, true, true);
    }

    @MustBeInvokedByOverriders
    protected void initAdapter() {
        if (initializedAdapter) {
            throw new IllegalStateException("adapter already initialized");
        }
        adapter.initAdapter();
        this.initializedAdapter = true;
    }

    protected void registerFields() {
        if (registeredFields) {
            throw new IllegalStateException("fields already registered");
        }

        Class<?> clazz = this.getClass();
        Set<Field> fields = new HashSet<>();
        Collections.addAll(fields, clazz.getDeclaredFields());
        Collections.addAll(fields, clazz.getFields());

        for (Field field : fields) {
            this.registerField(field);
        }

        this.registeredFields = true;
    }

    /* @formatter:off */
    private void registerField(@NotNull Field field) {
        if (!field.isAnnotationPresent(FeaturePresent.class)) {
            return;
        }

        String fieldDesc = "@" + FeaturePresent.class.getSimpleName() +
                " annotated field \"" + field.getName()
                + "\" in class " + this.getClass().getName();

        Class<?> type = field.getType();
        if (!DeviceFeature.class.isAssignableFrom(type)) {
            throw new InputException(fieldDesc + " must be assignable"
                    + " from " + this.getClass().getName());
        }

        /*
         * It would make no sense for @FeaturePresent annotated field to
         * be hidden. As such, it is required that they be public.
         */
        int mods = field.getModifiers();
        if (!Modifier.isPublic(mods)) {
            throw new InputException(fieldDesc + " must be public");
        }

        try {
            boolean statik = Modifier.isStatic(mods);
            Object obj = field.get(statik ? null : this);
            DeviceFeature<?> feature = (DeviceFeature<?>) obj;

            /*
             * There is a chance that this feature was registered before
             * registerField() was called for this field. While this is a
             * slim possibility, it would be infuriating to debug. As such,
             * perform this check before making the call to register.
             */
            if(!this.isRegistered(feature)) {
                this.registerFeature(feature);
            }
        } catch (IllegalAccessException e) {
            /*
             * The field is verified to be public before it is accessed by
             * this method. As such, this exception should never occur. If
             * it does, something has likely gone wrong in the JVM.
             */
            throw new InputException("this is a bug", e);
        }
    }
    /* @formatter:on */

    @Override
    public boolean isRegistered(@NotNull DeviceFeature<?> feature) {
        return registry.isRegistered(feature);
    }

    /* @formatter:off */
    @Override
    public @NotNull Collection<@NotNull RegisteredFeature<?, ?>>
            getFeatures() {
        return registry.getFeatures();
    }
    /* @formatter:on */

    /* @formatter:off */
    @Override
    public <S> @Nullable RegisteredFeature<?, S>
            getRegistered(@NotNull DeviceFeature<S> feature) {
        return registry.getRegistered(feature);
    }
    /* @formatter:on */

    /**
     * {@inheritDoc}
     * <p/>
     * <b>Note:</b> This method can be called before {@code InputDevice} is
     * finished constructing by the {@link #registerField(Field)} method.
     */
    /* @formatter:off */
    @Override
    public <F extends DeviceFeature<S>, S> @NotNull RegisteredFeature<F, S>
            registerFeature(@NotNull F feature) {
        RegisteredFeature<F, S> registeredFeature =
                registry.registerFeature(feature);
        if (registerFeatureCallback != null) {
            registerFeatureCallback.accept(feature);
        }
        return registeredFeature;
    }
    /* @formatter:on */

    @Override
    public void unregisterFeature(@NotNull DeviceFeature<?> feature) {
        registry.unregisterFeature(feature);
        if (unregisterFeatureCallback != null) {
            unregisterFeatureCallback.accept(feature);
        }
    }

    /**
     * Sets the callback for when a feature is registered. If this callback
     * was set <i>after</i> one or more features have been registered, it
     * will not be called for them. Current features will have to be fetched
     * via {@link #getFeatures()}.
     *
     * @param callback the code to execute when a feature is registered. A
     *                 value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     * @see #registerFeature(DeviceFeature)
     */
    public void onRegisterFeature(@Nullable Consumer<DeviceFeature<?>> callback) {
        this.registerFeatureCallback = callback;
    }

    /**
     * Sets the callback for when a feature is unregistered. If this callback
     * was set <i>after</i> one or more features have been unregistered, it
     * will not be called for them. Current features will have to be fetched
     * via {@link #getFeatures()}.
     *
     * @param callback the code to execute when a feature is unregistered. A
     *                 value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     * @see #unregisterFeature(DeviceFeature)
     */
    public void onUnregisterFeature(@Nullable Consumer<DeviceFeature<?>> callback) {
        this.unregisterFeatureCallback = callback;
    }

    /**
     * Sets the callback for when this device connects. If this callback was
     * set <i>after</i> the device has connected, it will not be called.
     *
     * @param callback the code to execute when this device connects. A
     *                 value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     * @see #isConnected()
     */
    public void onConnect(@Nullable Runnable callback) {
        this.connectCallback = callback;
    }

    /**
     * Sets the callback for when this device disconnects. If this callback
     * was set <i>after</i> the device has disconnected, it will not be called.
     *
     * @param callback the code to execute when this device disconnects. A
     *                 value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     * @see #isConnected()
     */
    public void onDisconnect(@Nullable Runnable callback) {
        this.disconnectCallback = callback;
    }

    /**
     * For this method to return up-to-date values, this device
     * must first be updated via {@link #poll()} beforehand.
     *
     * @return {@code true} if this device is currently connected,
     * {@code false} otherwise.
     */
    public boolean isConnected() {
        return this.connected;
    }

    /**
     * Performs a <i>single</i> query of input information from the device
     * adapter and updates all features registered to the input device. It
     * is recommended to call this method once every application update.
     *
     * @see #isConnected()
     */
    @MustBeInvokedByOverriders
    public synchronized void poll() {
        adapter.pollDevice();
        registry.updateFeatures();

        boolean wasConnected = this.connected;
        this.connected = adapter.isDeviceConnected();
        if (connected && !wasConnected && connectCallback != null) {
            connectCallback.run();
        } else if (!connected && wasConnected && disconnectCallback != null) {
            disconnectCallback.run();
        }
    }

}
