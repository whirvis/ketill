package io.ketill;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
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
 * @see IoFeature
 * @see IoDeviceAdapter
 * @see IoDeviceSeeker
 */
public abstract class IoDevice implements FeatureRegistry {

    private final @NotNull String typeId;

    private final @NotNull Subject<IoDeviceEvent> subject;

    /**
     * The observer for this I/O device. This should be used to emit events
     * to listeners when they occur.
     * <p>
     * This field is {@code protected} so it is visible to child classes.
     * It is also passed to the internal state of I/O features, allowing
     * them to emit their own events from this device.
     */
    protected final @NotNull IoDeviceObserver observer;

    private final MappedFeatureRegistry registry;
    private final IoDeviceAdapter<IoDevice> adapter;
    private boolean initializedAdapter;
    private boolean registeredFields;
    private boolean connected;

    /**
     * Constructs a new {@code IoDevice}.
     *
     * @param typeId          the device type ID.
     * @param adapterSupplier the device adapter supplier.
     * @param registerFields  {@code true} if the constructor should call
     *                        {@link #registerFields()}. If {@code false},
     *                        the extending class must call it if it desires
     *                        the functionality of {@link FeaturePresent}.
     * @param initAdapter     {@code true} if the constructor should call
     *                        {@link #initAdapter()}. If {@code false}, the
     *                        extending class <b>must</b> call it.
     * @throws NullPointerException     if {@code typeId} or
     *                                  {@code adapterSupplier}
     *                                  are {@code null}; if the adapter
     *                                  given by {@code adapterSupplier}
     *                                  is {@code null}.
     * @throws IllegalArgumentException if {@code typeId} is empty or
     *                                  contains whitespace.
     */
    @SuppressWarnings("unchecked")
    public IoDevice(@NotNull String typeId,
                    @NotNull AdapterSupplier<?> adapterSupplier,
                    boolean registerFields, boolean initAdapter) {
        this.typeId = Objects.requireNonNull(typeId,
                "typeId cannot be null");
        if (typeId.isEmpty()) {
            throw new IllegalArgumentException("id cannot be empty");
        } else if (!typeId.matches("\\S+")) {
            throw new IllegalArgumentException("id cannot contain whitespace");
        }

        /*
         * This must be created before doing anything else. It is possible
         * an event will be fired from code executed in this constructor.
         */
        this.subject = PublishSubject.create();
        this.observer = new IoDeviceObserver(this, subject);

        this.registry = new MappedFeatureRegistry(observer);

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
     * Constructs a new {@code IoDevice}, automatically registers all fields
     * marked with the {@link FeaturePresent} annotation, and initializes the
     * adapter.
     *
     * @param typeId          the device type ID.
     * @param adapterSupplier the device adapter supplier.
     * @throws NullPointerException     if {@code typeId} or
     *                                  {@code adapterSupplier}
     *                                  are {@code null}.
     * @throws IllegalArgumentException if {@code typeId} is empty or
     *                                  contains whitespace.
     */
    public IoDevice(@NotNull String typeId,
                    @NotNull AdapterSupplier<?> adapterSupplier) {
        this(typeId, adapterSupplier, true, true);
    }

    /**
     * Returns the type ID of this device.
     * <p>
     * <b>Note:</b> Two instances of the same {@code IoDevice} class should
     * share an identical type ID. For example, two XBOX controllers would
     * have the type ID {@code "xbox"}.
     *
     * @return the type ID of this device.
     */
    public final @NotNull String getTypeId() {
        return this.typeId;
    }

    /**
     * Subscribes to events emitted from this I/O device.
     *
     * @param eventClazz the event type class to listen for. Only events of
     *                   this type and those extending it will be emitted
     *                   to {@code callable}.
     * @param callback   the code to execute when an event of the desired
     *                   type is emitted by the device.
     * @param <T>        the event type.
     * @return the new {@link IoDisposable} instance, which can be used to
     * dispose the subscription at any time.
     * @throws NullPointerException if {@code eventClazz} or {@code callback}
     *                              are {@code null}.
     * @see IoDeviceEvent
     */
    /* @formatter:off */
    @SuppressWarnings("unchecked")
    public final <T extends IoDeviceEvent> @NotNull IoDisposable
            subscribeEvents(@NotNull Class<T> eventClazz,
                            @NotNull Consumer<T> callback) {
        Objects.requireNonNull(eventClazz, "eventClazz cannot be null");
        Objects.requireNonNull(callback, "callback cannot be null");
        Disposable rxDisposable =
                subject.filter(event -> eventClazz.isAssignableFrom(event.getClass()))
                .map(obj -> (T) obj).subscribe(callback::accept);
        return new IoDisposable(rxDisposable);
    }
    /* @formatter:on */

    /**
     * Subscribes to all events emitted from this I/O device.
     *
     * @param callback the code to execute when an event is emitted by the
     *                 device.
     * @return the new {@link IoDisposable} instance, which can be used to
     * dispose the subscription at any time.
     * @throws NullPointerException if {@code callback} is {@code null}.
     */
    /* @formatter:off */
    public final @NotNull IoDisposable
            subscribeEvents(@NotNull Consumer<IoDeviceEvent> callback) {
        Objects.requireNonNull(callback, "callback cannot be null");
        return this.subscribeEvents(IoDeviceEvent.class, callback);
    }
    /* @formatter:on */

    /**
     * Initializes the adapter instantiated at construction.
     * <p>
     * <b>Note:</b> This method is called automatically only if the
     * argument for {@code initAdapter} at construction was {@code true}.
     * Otherwise, a child class must call this method.
     *
     * @throws IllegalStateException if this has already been called.
     * @see #adapterInitialized()
     */
    @MustBeInvokedByOverriders
    protected void initAdapter() {
        if (initializedAdapter) {
            throw new IllegalStateException("adapter already initialized");
        }
        adapter.initAdapter();
        this.initializedAdapter = true;

        this.adapterInitialized();
        observer.onNext(new AdapterInitializedEvent(this));
    }

    /**
     * Called when the adapter has been initialized. This will be called
     * before the corresponding event is emitted to subscribers.
     */
    protected void adapterInitialized() {
        /* optional implement */
    }

    /**
     * Registers all fields annotated with {@link FeaturePresent} that are
     * present in this class.
     * <p>
     * <b>Note:</b> This method is called automatically only if the
     * argument for {@code registerFields} at construction was {@code true}.
     * Otherwise, a child class must call this method.
     *
     * @throws IllegalStateException if this has already been called.
     * @see #registerFeature(IoFeature)
     * @see #fieldsRegistered()
     */
    @MustBeInvokedByOverriders
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

        this.fieldsRegistered();
        observer.onNext(new FieldsRegisteredEvent(this));
    }

    /**
     * Called when all fields present in this class annotated with
     * {@link FeaturePresent} have been registered. This will be called
     * before the corresponding event is emitted to subscribers.
     */
    protected void fieldsRegistered() {
        /* optional implement */
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
        Class<?> clazz = field.getDeclaringClass();
        boolean sharePkg = clazz.getPackage() == IoDevice.class.getPackage();
        if(!Modifier.isPublic(clazz.getModifiers()) && !sharePkg) {
            String msg = "class " + clazz.getName() + " declaring ";
            msg += field.getName() + " must be public";
            throw new KetillException(msg);
        }

        String fieldDesc = "@" + FeaturePresent.class.getSimpleName();
        fieldDesc += " annotated field \"" + field.getName() + "\"";
        fieldDesc += " in class " + this.getClass().getName();

        if (!IoFeature.class.isAssignableFrom(field.getType())) {
            String msg = fieldDesc + " must be assignable from ";
            msg += IoFeature.class.getName();
            throw new KetillException(msg);
        }

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
            IoFeature<?, ?> feature = (IoFeature<?, ?>) obj;

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
     * owning {@code featureState} is currently registered to this device,
     * {@code null} is returned and no exception will be thrown.
     *
     * @param featureState the state of the feature to fetch. This can be
     *                     either the internal state of the container state.
     * @return the {@code feature} which owns {@code featureState},
     * {@code null} if no such feature is currently registered.
     * @throws NullPointerException if {@code featureState} is {@code null}.
     * @throws KetillException      if {@code featureState} is an instance
     *                              of {@link IoFeature}.
     */
    public final @Nullable IoFeature<?, ?> getFeature(Object featureState) {
        Objects.requireNonNull(featureState, "featureState cannot be null");
        if (featureState instanceof IoFeature) {
            String msg = "cannot get a feature's feature";
            msg += ", did you mean getState(IoFeature)?";
            throw new KetillException(msg);
        }
        for (RegisteredIoFeature<?, ?, ?> registered :
                registry.getFeatureRegistrations()) {
            if (registered.internalState == featureState ||
                    registered.containerState == featureState) {
                return registered.feature;
            }
        }
        return null;
    }

    /**
     * Returns if this device, with its adapter provided at construction,
     * supports a given feature. A feature is considered supported if it
     * currently has a mapping assigned by its adapter.
     * <p>
     * When a feature is not supported, any reads will return its initial
     * state (or the last value before being unmapped.) If the state of a
     * feature is writable, any writes will effectively be a no-op.
     * <p>
     * <b>Note:</b> It is possible for a feature to be considered supported
     * <i>without</i> being registered to this device.
     *
     * @param feature the feature to check.
     * @return {@code true} if {@code feature} is supported, {@code false}
     * otherwise.
     * @throws NullPointerException if {@code feature} is {@code null}.
     * @see #isFeatureSupported(Object)
     */
    public final boolean isFeatureSupported(@NotNull IoFeature<?, ?> feature) {
        return registry.hasMapping(feature);
    }

    /**
     * Returns if this device supports a given feature.
     * <p>
     * When a feature is not supported, reading from its state will yield
     * the initial value (or the last value it had before being unmapped.)
     * Furthermore, if the state said feature is writable, any writes should
     * effectively be a no-op.
     * <p>
     * <b>Note:</b> It is possible for a feature to be considered supported
     * <i>without</i> being registered to this device. It is also possible
     * for a feature to be supported, but this device not have the capability
     * for it. For example, an off-brand which excludes rumble motors from
     * a controller that usually includes them.
     *
     * @param featureState the state of the feature to check. This can be
     *                     either the internal state of the container state.
     * @return {@code true} if the feature which instantiated
     * {@code featureState} is supported, {@code false} otherwise.
     * @throws NullPointerException if {@code featureState} is {@code null}.
     * @see #getState(IoFeature)
     * @see #isFeatureSupported(IoFeature)
     */
    public final boolean isFeatureSupported(@NotNull Object featureState) {
        Objects.requireNonNull(featureState, "featureState cannot be null");
        if (featureState instanceof IoFeature<?, ?>) {
            /*
             * If this overload method is somehow called with an IoFeature,
             * likely due to a mistaken cast, just call the original method.
             * This is to prevent headaches on the side of the user.
             */
            return this.isFeatureSupported((IoFeature<?, ?>) featureState);
        }
        IoFeature<?, ?> feature = this.getFeature(featureState);
        return feature != null && this.isFeatureSupported(feature);
    }

    @Override
    public final boolean isFeatureRegistered(@NotNull IoFeature<?, ?> feature) {
        return registry.isFeatureRegistered(feature);
    }

    @Override
    public final boolean isFeatureWithIdRegistered(@NotNull String id) {
        return registry.isFeatureWithIdRegistered(id);
    }

    @Override
    public final int getFeatureCount() {
        return registry.getFeatureCount();
    }

    @Override
    public final @Nullable IoFeature<?, ?> getFeatureById(@NotNull String id) {
        return registry.getFeatureById(id);
    }

    @Override
    public final @NotNull Collection<@NotNull IoFeature<?, ?>> getFeatures() {
        return registry.getFeatures();
    }

    /* @formatter:off */
    @Override
    public final <Z, S> @Nullable RegisteredIoFeature<?, Z, S>
            getFeatureRegistration(@NotNull IoFeature<Z, S> feature) {
        return registry.getFeatureRegistration(feature);
    }
    /* @formatter:on */

    /* @formatter:off */
    @Override
    public final @NotNull Collection<@NotNull RegisteredIoFeature<?, ?, ?>>
            getFeatureRegistrations() {
        return registry.getFeatureRegistrations();
    }
    /* @formatter:on */

    /**
     * {@inheritDoc}
     * <p>
     * <b>Note:</b> Extending classes needing to access the internal state
     * of a feature can do so via {@link #getInternalState(IoFeature)}.
     */
    @Override
    public final <S> @NotNull S getState(@NotNull IoFeature<?, S> feature) {
        return registry.getState(feature);
    }

    /**
     * Returns the internal state of an I/O feature.
     * <p>
     * <b>Note:</b> This method exists <i>only</i> for the benefit of
     * extending classes. The internal state of an I/O feature should
     * (usually) <i>not</i> be publicly accessible.
     *
     * @param feature the feature whose state to fetch.
     * @param <Z>     the internal state type.
     * @return the internal state of {@code feature}.
     * @throws NullPointerException  if {@code feature} is {@code null}.
     * @throws IllegalStateException if {@code feature} is not registered.
     * @see #getState(IoFeature)
     */
    protected final <Z> Z getInternalState(@NotNull IoFeature<Z, ?> feature) {
        return registry.getInternalState(feature);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Note:</b> When using {@link FeaturePresent}, it is possible for
     * this method to be called by {@link #registerField(Field)} before an
     * {@code IoDevice} has finished construction.
     *
     * @see #featureRegistered(RegisteredIoFeature)
     */
    /* @formatter:off */
    @Override
    public <F extends IoFeature<Z, S>, Z, S>
            @NotNull RegisteredIoFeature<F, Z, S>
            registerFeature(@NotNull F feature) {
        Objects.requireNonNull(feature, "feature cannot be null");
        RegisteredIoFeature<F, Z, S> registered =
                registry.registerFeature(feature);

        this.featureRegistered(registered);
        observer.onNext(new IoFeatureRegisterEvent(this, registered));
        return registered;
    }
    /* @formatter:on */

    /**
     * Called when a feature is registered. This will be called before
     * the corresponding event is emitted to subscribers.
     * <p>
     * <b>Note:</b> It is possible for this method to be called before
     * an {@code IoDevice} has finished construction.
     *
     * @param registered the registered feature.
     * @see #getInternalState(IoFeature)
     */
    protected void featureRegistered(@NotNull RegisteredIoFeature<?, ?, ?> registered) {
        /* optional implement */
    }

    /**
     * {@inheritDoc}
     *
     * @see #featureUnregistered(IoFeature)
     */
    @Override
    public void unregisterFeature(@NotNull IoFeature<?, ?> feature) {
        registry.unregisterFeature(feature);
        this.featureUnregistered(feature);
        observer.onNext(new IoFeatureUnregisterEvent(this, feature));
    }

    /**
     * Called when a feature is unregistered. This will be called before
     * the corresponding event is emitted to subscribers.
     *
     * @param feature the unregistered feature.
     */
    protected void featureUnregistered(@NotNull IoFeature<?, ?> feature) {
        /* optional implement */
    }

    /**
     * Returns if this device is currently connected.
     * <p>
     * <b>Note:</b> This method returns up-to-date values without needing
     * to call {@link #poll()}.
     *
     * @return {@code true} if this device is currently connected,
     * {@code false} otherwise.
     * @see #deviceConnected()
     * @see #deviceDisconnected()
     */
    public final boolean isConnected() {
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
     * Called when this device is connected. This will be called before
     * the corresponding event is emitted to subscribers.
     */
    protected void deviceConnected() {
        /* optional implement */
    }

    /**
     * Called when this device is disconnected. This will be called before
     * the corresponding event is emitted to subscribers.
     */
    protected void deviceDisconnected() {
        /* optional implement */
    }

    /**
     * Performs a <i>single</i> query on the device adapter and updates all
     * features registered to this I/O device. It is recommended to call this
     * method once every application update.
     *
     * @throws KetillException if an error occurs while polling the adapter.
     * @see #isConnected()
     */
    @MustBeInvokedByOverriders
    public synchronized void poll() {
        try {
            adapter.pollDevice();
        } catch (Throwable cause) {
            if (cause instanceof KetillException) {
                throw (KetillException) cause;
            }
            String msg = "error while polling ";
            msg += adapter.getClass().getName();
            throw new KetillException(msg, cause);
        }

        boolean wasConnected = this.connected;
        this.connected = this.isConnected();
        if (connected && !wasConnected) {
            this.deviceConnected();
            observer.onNext(new IoDeviceConnectEvent(this));
        } else if (!connected && wasConnected) {
            this.deviceDisconnected();
            observer.onNext(new IoDeviceDisconnectEvent(this));
        }

        registry.updateFeatures();
    }

    /* @formatter:off */
    @Override
    public String toString() {
        return ToStringUtils.getJoiner(this)
                .add("typeId='" + typeId + "'")
                .add("initializedAdapter=" + initializedAdapter)
                .add("registeredFields=" + registeredFields)
                .add("connected=" + connected)
                .toString();
    }
    /* @formatter:on */

}
