package com.whirvis.kibasan;

import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
     * @throws NullPointerException if {@code id} or {@code adapterSupplier}
     *                              are {@code null}; if the adapter supplied
     *                              by {@code adapterSupplier} is {@code null}.
     */
    @SuppressWarnings("unchecked")
    public InputDevice(@NotNull String id,
                       @NotNull AdapterSupplier<?> adapterSupplier,
                       boolean registerFields, boolean initAdapter) {
        this.id = Objects.requireNonNull(id, "id");
        this.registry = new MappedFeatureRegistry(this);

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
     * @throws NullPointerException if {@code id} or {@code adapterSupplier}
     *                              are {@code null}.
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
        Class<?> clazz = this.getClass();
        Set<Field> fields = new HashSet<>();
        Collections.addAll(fields, clazz.getDeclaredFields());
        Collections.addAll(fields, clazz.getFields());

        for (Field field : fields) {
            this.registerField(field);
        }
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
            this.registerFeature((DeviceFeature<?>) obj);
        } catch (IllegalAccessException e) {
            throw new InputException("failure to access"
                    + " @" + FeaturePresent.class.getSimpleName()
                    + " annotated field", e);
        }
    }
    /* @formatter:on */

    @Override
    public boolean isRegistered(@NotNull DeviceFeature<?> feature) {
        return registry.isRegistered(feature);
    }

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
        return registry.registerFeature(feature);
    }
    /* @formatter:on */

    @Override
    public void unregisterFeature(@NotNull DeviceFeature<?> feature) {
        registry.unregisterFeature(feature);
    }

    public boolean isConnected() {
        return adapter.isDeviceConnected();
    }

    /**
     * Performs a <i>single</i> query of input information from the device
     * adapter and updates all features registered to the input device. It
     * is recommended to call this method once every application update.
     *
     * @see #isConnected()
     */
    @MustBeInvokedByOverriders
    public void poll() {
        adapter.pollDevice();
        registry.updateFeatures();
    }

}
