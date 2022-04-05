package io.ketill;

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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A device which can send and receive I/O data.
 * <p>
 * Examples of I/O devices include, but are not limited to: keyboards, mice,
 * XBOX controllers, etc. By design, an I/O device supports no features by
 * default. Rather, an extending class must provide them. The responsibility
 * of providing support for a feature is designed to the device adapter.
 * <p>
 * <b>Note:</b> For data to stay up-to-date, the device must be polled
 * periodically via the {@link #poll()} method. It is recommended to
 * poll the device once every application update.
 *
 * @see IoDeviceSeeker
 * @see IoDeviceAdapter
 * @see IoFeature
 * @see FeaturePresent
 */
public abstract class IoDevice implements FeatureRegistry {

    public final @NotNull String id;

    private final MappedFeatureRegistry registry;
    private final IoDeviceAdapter<IoDevice> adapter;
    private boolean initializedAdapter;
    private boolean registeredFields;
    private boolean connected;

    private @Nullable BiConsumer<IoDevice, IoFeature<?>> registerFeatureCallback;
    private @Nullable BiConsumer<IoDevice, IoFeature<?>> unregisterFeatureCallback;
    private @Nullable BiConsumer<IoDevice, Throwable> errorCallback;
    private @Nullable Consumer<IoDevice> connectCallback;
    private @Nullable Consumer<IoDevice> disconnectCallback;

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
    public IoDevice(@NotNull String id,
                    @NotNull AdapterSupplier<?> adapterSupplier,
                    boolean registerFields, boolean initAdapter) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        if (id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be empty");
        } else if (!id.matches("\\S+")) {
            throw new IllegalArgumentException("id cannot contain whitespace");
        }

        this.registry = new MappedFeatureRegistry();

        /*
         * While this is an unchecked cast, the template requires that the
         * type extend IoDevice. As such, this cast is safe to perform.
         */
        Objects.requireNonNull(adapterSupplier,
                "adapterSupplier cannot be null");
        AdapterSupplier<IoDevice> castedSupplier =
                (AdapterSupplier<IoDevice>) adapterSupplier;
        this.adapter = castedSupplier.get(this, registry);
        Objects.requireNonNull(adapter,
                "value supplied by adapterSupplier cannot be null");

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
    public IoDevice(@NotNull String id,
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

        /*
         * It is possible for a class to not be public, and instead
         * be package private. It is impossible to get the value of
         * fields in these classes, even if they are public.
         *
         * However, a class can be package private if it resides in
         * the same package as IoDevice. This makes it possible for
         * the test classes to remain package private.
         *
         * Furthermore, this check should be run only if there are
         * fields which will be registered. It would not make sense
         * to force the user to make their class public if they did
         * not make use of the feature which requires it.
         */
        /* @formatter:off */
        Class<?> clazz = field.getDeclaringClass();
        boolean sharePkg = clazz.getPackage() == IoDevice.class.getPackage();
        if(!Modifier.isPublic(clazz.getModifiers()) && !sharePkg) {
            throw new KetillException("class " + clazz.getName() +
                    " declaring " + field.getName() +" must be public");
        }
        /* @formatter:on */

        String fieldDesc = "@" + FeaturePresent.class.getSimpleName();
        fieldDesc += " annotated field \"" + field.getName() + "\"";
        fieldDesc += " in class " + this.getClass().getName();

        /* @formatter:off */
        if (!IoFeature.class.isAssignableFrom(field.getType())) {
            throw new KetillException(fieldDesc + " must be assignable from "
                    + this.getClass().getName());
        }
        /* @formatter:on */

        /*
         * It would make no sense for @FeaturePresent annotated
         * field to be hidden. As such, it is required that they
         * be public; even if it resides in the same package.
         */
        int mods = field.getModifiers();
        if (!Modifier.isPublic(mods)) {
            throw new KetillException(fieldDesc + " must be public");
        }

        try {
            boolean statik = Modifier.isStatic(mods);
            Object obj = field.get(statik ? null : this);
            IoFeature<?> feature = (IoFeature<?>) obj;

            /*
             * There is a chance that this feature was registered before
             * registerField() was called for this field. While this is a
             * slim possibility, it would be infuriating to debug. As such,
             * perform this check before making the call to register.
             */
            if (!this.isFeatureRegistered(feature)) {
                this.registerFeature(feature);
            }
        } catch (IllegalAccessException e) {
            /*
             * The field is verified to be public before it is accessed by
             * this method. As such, this exception should never occur. If
             * it does, something has likely gone wrong in the JVM.
             */
            throw new KetillException("this is a bug", e);
        }
    }
    /* @formatter:on */

    /**
     * Returns the feature associated with the given state. If no feature
     * owning {@code featureState} is registered to this device, {@code null}
     * is returned and no exception is thrown.
     *
     * @param featureState the state of the feature to fetch.
     * @return the {@code feature} which owns {@code featureState},
     * {@code null} if no such feature is currently registered.
     * @throws NullPointerException          if {@code featureState}
     *                                       is {@code null}.
     * @throws UnsupportedOperationException if {@code featureState} is
     *                                       an {@link IoFeature} instance.
     */
    public IoFeature<?> getFeature(Object featureState) {
        Objects.requireNonNull(featureState, "featureState cannot be null");
        if (featureState instanceof IoFeature<?>) {
            String msg = "did you mean getState(IoFeature)?";
            throw new UnsupportedOperationException(msg);
        }
        for (RegisteredFeature<?, ?> registered : registry.getFeatures()) {
            if (registered.state == featureState) {
                return registered.feature;
            }
        }
        return null;
    }

    /**
     * Returns if this device, with its adapter provided at construction,
     * supports the specified feature. A feature is considered supported
     * if it currently has a mapping assigned by its adapter.
     * <p>
     * When a feature is not supported, any reads will return its initial
     * state (or the last value before being unmapped.) If the state of a
     * feature is writable, any writes will effectively be a no-op.
     *
     * @param feature the feature to check.
     * @return {@code true} if {@code feature} is supported, {@code false}
     * otherwise.
     * @throws NullPointerException if {@code feature} is {@code null}.
     * @see #isFeatureSupported(Object)
     */
    public boolean isFeatureSupported(@NotNull IoFeature<?> feature) {
        return registry.hasMapping(feature);
    }

    /**
     * Returns if this device, with its adapter provided at construction,
     * supports the specified feature. A feature is considered supported
     * if it currently has a mapping assigned by its adapter.
     * <p>
     * When a feature is not supported, any reads will return its initial
     * state (or the last value before being unmapped.) If the state of a
     * feature is writable, any writes will effectively be a no-op.
     *
     * @param featureState the state of the feature to check.
     * @return {@code true} if the feature which instantiated
     * {@code featureState} is supported, {@code false} otherwise.
     * @throws NullPointerException if {@code featureState} is {@code null}.
     * @see #getState(IoFeature)
     * @see #isFeatureSupported(IoFeature)
     */
    public boolean isFeatureSupported(@NotNull Object featureState) {
        IoFeature<?> feature = this.getFeature(featureState);
        return feature != null && this.isFeatureSupported(feature);
    }

    @Override
    public boolean isFeatureRegistered(@NotNull IoFeature<?> feature) {
        return registry.isFeatureRegistered(feature);
    }

    @Override
    public int getFeatureCount() {
        return registry.getFeatureCount();
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
            getFeatureRegistration(@NotNull IoFeature<S> feature) {
        return registry.getFeatureRegistration(feature);
    }
    /* @formatter:on */

    /**
     * {@inheritDoc}
     * <p>
     * <b>Note:</b> This method can be called before {@code IoDevice} is
     * finished constructing by the {@link #registerField(Field)} method.
     */
    /* @formatter:off */
    @Override
    public <F extends IoFeature<S>, S> @NotNull RegisteredFeature<F, S>
            registerFeature(@NotNull F feature) {
        Objects.requireNonNull(feature, "feature cannot be null");
        RegisteredFeature<F, S> registered = registry.registerFeature(feature);
        this.featureRegistered(registered);
        if (registerFeatureCallback != null) {
            registerFeatureCallback.accept(this, feature);
        }
        return registered;
    }
    /* @formatter:on */

    /**
     * Called when a feature is registered. Overriding this method allows
     * for an I/O device to know when a feature has been registered without
     * needing to set themselves as the callback.
     *
     * @param registered the registered feature.
     */
    @SuppressWarnings("unused")
    protected void featureRegistered(@NotNull RegisteredFeature<?, ?> registered) {
        /* optional implement */
    }

    @Override
    public void unregisterFeature(@NotNull IoFeature<?> feature) {
        registry.unregisterFeature(feature);
        this.featureUnregistered(feature);
        if (unregisterFeatureCallback != null) {
            unregisterFeatureCallback.accept(this, feature);
        }
    }

    /**
     * Called when a feature is unregistered. Overriding this method allows
     * for an I/O device to know when a feature has been unregistered without
     * needing to set themselves as the callback.
     *
     * @param feature the unregistered feature.
     */
    @SuppressWarnings("unused")
    protected void featureUnregistered(@NotNull IoFeature<?> feature) {
        /* optional implement */
    }

    /**
     * Sets the callback for when a feature is registered. If this callback
     * was set <i>after</i> one or more features have been registered, it
     * will not be called for them. Current features will have to be fetched
     * via {@link #getFeatures()}.
     * <p>
     * <b>Note:</b> Extending classes wishing to listen for this event should
     * override {@link #featureRegistered(RegisteredFeature)}. The callback
     * is for users.
     *
     * @param callback the code to execute when a feature is registered. A
     *                 value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     * @see #registerFeature(IoFeature)
     */
    public final void onRegisterFeature(@Nullable BiConsumer<IoDevice,
            IoFeature<?>> callback) {
        this.registerFeatureCallback = callback;
    }

    /**
     * Sets the callback for when a feature is unregistered. If this callback
     * was set <i>after</i> one or more features have been unregistered, it
     * will not be called for them. Current features will have to be fetched
     * via {@link #getFeatures()}.
     * <p>
     * <b>Note:</b> Extending classes wishing to listen for this event should
     * override {@link #featureUnregistered(IoFeature)}. The callback is for
     * users.
     *
     * @param callback the code to execute when a feature is unregistered. A
     *                 value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     * @see #unregisterFeature(IoFeature)
     */
    public final void onUnregisterFeature(@Nullable BiConsumer<IoDevice,
            IoFeature<?>> callback) {
        this.unregisterFeatureCallback = callback;
    }

    /**
     * Sets the callback for when an error occurs in {@link #poll()}. By
     * default, a wrapping {@code KetillException} will be constructed for
     * the original error and thrown.
     *
     * @param callback the code to execute when an error occurs. A value
     *                 of {@code null} is permitted, and will result in a
     *                 wrapping {@code KetillException} being thrown.
     */
    public final void onPollError(@Nullable BiConsumer<IoDevice,
            Throwable> callback) {
        this.errorCallback = callback;
    }

    /**
     * Sets the callback for when this device connects. If this callback was
     * set <i>after</i> the device has connected, it will not be called.
     * <p>
     * <b>Note:</b> Extending classes wishing to listen for this event should
     * override {@link #deviceConnected()}. The callback is for users.
     *
     * @param callback the code to execute when this device connects. A
     *                 value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     * @see #isConnected()
     */
    public final void onConnect(@Nullable Consumer<IoDevice> callback) {
        this.connectCallback = callback;
    }

    /**
     * Sets the callback for when this device disconnects. If this callback
     * was set <i>after</i> the device has disconnected, it will not be called.
     * <p>
     * <b>Note:</b> Extending classes wishing to listen for this event should
     * override {@link #deviceDisconnected()}. The callback is for users.
     *
     * @param callback the code to execute when this device disconnects. A
     *                 value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     * @see #isConnected()
     */
    public final void onDisconnect(@Nullable Consumer<IoDevice> callback) {
        this.disconnectCallback = callback;
    }

    /**
     * Called when this device is first connected. Overriding this method
     * allows for an I/O device to know when it has been connected without
     * needing to set themselves as the callback.
     */
    protected void deviceConnected() {
        /* optional implement */
    }

    /**
     * Called when this device is disconnected. Overriding this method
     * allows for an I/O device to know when it has been disconnected
     * without needing to set themselves as the callback.
     */
    protected void deviceDisconnected() {
        /* optional implement */
    }

    /**
     * <b>Note:</b> This method returns up-to-date values without the need to
     * call {@link #poll()}.
     *
     * @return {@code true} if this device is currently connected,
     * {@code false} otherwise.
     */
    public boolean isConnected() {
        /*
         * Before, this method returned the value of the "connected"
         * field. This made poll() required for an up-to-date value.
         * This in turn resulted a bug in device seekers which used
         * isConnected() to check if a device should be forgotten.
         *
         * If a call to poll() wasn't made after a device was first
         * discovered, before the next call to seek() was made, the
         * seeker would immediately forget the device. Afterwards,
         * the device would immediately be rediscovered (as it was
         * never disconnected in the first place.) This would then
         * occur repeatedly for each two calls to seek().
         */
        return adapter.isDeviceConnected();
    }

    /**
     * Performs a <i>single</i> query from the device adapter and updates all
     * features registered to the I/O device. It is recommended to call this
     * method once every application update.
     *
     * @see #isConnected()
     */
    @MustBeInvokedByOverriders
    public synchronized void poll() {
        try {
            adapter.pollDevice();
        } catch (Throwable cause) {
            if (errorCallback != null) {
                errorCallback.accept(this, cause);
            } else {
                throw new KetillException("error in IoDevice", cause);
            }
        }

        boolean wasConnected = this.connected;
        this.connected = adapter.isDeviceConnected();
        if (connected && !wasConnected) {
            this.deviceConnected();
            if (connectCallback != null) {
                connectCallback.accept(this);
            }
        } else if (!connected && wasConnected) {
            this.deviceDisconnected();
            if (disconnectCallback != null) {
                disconnectCallback.accept(this);
            }
        }

        registry.updateFeatures();
    }

}
