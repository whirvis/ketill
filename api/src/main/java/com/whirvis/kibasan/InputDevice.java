package com.whirvis.kibasan;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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
    private final DeviceAdapter<InputDevice> adapter;
    private final MappedFeatureRegistry registry;

    /**
     * @param id      the device ID.
     * @param adapter the device adapter.
     */
    @SuppressWarnings("unchecked")
    public InputDevice(@NotNull String id, @NotNull DeviceAdapter<?> adapter) {
        this.id = id;
        this.adapter = (DeviceAdapter<InputDevice>) adapter;
        this.registry = new MappedFeatureRegistry(this);

        Class<?> clazz = this.getClass();
        for (Field field : ReflectionUtils.getAllFields(clazz)) {
            this.registerFeatureField(field);
        }

        /*  */
        this.adapter.initAdapter(this, registry);
    }

    /* @formatter:off */
    private void registerFeatureField(@NotNull Field field) {
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
     * finished constructing by the {@link #registerFeatureField(Field)}
     * method. As such, extending classes should take care to write code
     * around this fact if they override this method.
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
        return adapter.isDeviceConnected(this);
    }

    /**
     * Performs a <i>single</i> query of input information from the device
     * adapter and updates all features registered to the input device. It
     * is recommended to call this method once every application update.
     *
     * @see #isConnected()
     */
    public void poll() {
        adapter.pollDevice(this);
        registry.updateFeatures();
    }

}
