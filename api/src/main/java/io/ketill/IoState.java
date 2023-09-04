package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The state of an {@link IoFeature}.
 * <p>
 * Examples include (but are not limited to): if a button is pressed, the
 * position of an analog stick, the intensity of a rumble motor, or an LED's
 * display number.
 * <p>
 * <b>For states with no internals:</b> Use {@link #NO_INTERNALS} as the
 * argument for {@code internals} at construction.
 *
 * @param <I> the internal data type.
 * @see BuiltIn
 * @see IoDevice#getState(IoFeature)
 */
public abstract class IoState<I> {

    /**
     * When present, indicates to an {@link IoDevice} that a field contains
     * the state of a built-in {@link IoFeature}. Using this annotation also
     * ensures the field has proper form for an I/O state declaration.
     * <p>
     * <b>Requirements</b>
     * <p>
     * This annotation requires that:
     * <ul>
     *     <li>The field must be {@code public} and {@code final}.</li>
     *     <li>The type must be assignable from {@code IoState}.</li>
     *     <li>The field cannot be {@code static}.</li>
     * </ul>
     * <p>
     * If these requirements are not met, an appropriate exception shall
     * be thrown by the constructor of {@code IoDevice}. In addition, they
     * must not be {@code null}. However, the order of class instantiation
     * prevents this from being enforced at runtime.
     * <p>
     * <b>Recommendations</b>
     * <p>
     * The recommended naming convention for these fields is camel case,
     * with the name being the ID of its I/O feature. This makes their
     * name shorter, and also clearly distinguishes them from their sister
     * fields, which contain the {@code IoFeature} they represent.
     * <p>
     * <b>Example</b>
     * <p>
     * The following is an example use of this annotation.
     * <pre>
     * &#47;* note: Gamepad extends IoDevice *&#47;
     * class XboxController extends Gamepad {
     *
     *     &#47;* note: GamepadButton extends IoFeature *&#47;
     *     &#64;IoFeature.BuiltIn
     *     public static final GamepadButton
     *             BUTTON_A = new GamepadButton("a"),
     *             BUTTON_B = new GamepadButton("b"),
     *             BUTTON_X = new GamepadButton("x"),
     *             BUTTON_Y = new GamepadButton("y");
     *
     *     &#47;* note: GamepadButtonState extends IoState *&#47;
     *     &#64;IoState.BuiltIn
     *     public final GamepadButtonState
     *             a = this.addFeature(BUTTON_A),
     *             b = this.addFeature(BUTTON_B),
     *             x = this.addFeature(BUTTON_X),
     *             y = this.addFeature(BUTTON_Y);
     *
     * }
     * </pre>
     *
     * @see IoDevice#getState(IoFeature)
     */
    @Documented
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface BuiltIn {
        /* this annotation has no attributes */
    }

    @VisibleForTesting
    static void validateBuiltInField(@NotNull Field field) {
        if (!field.isAnnotationPresent(BuiltIn.class)) {
            return;
        }

        String fieldDesc = "@" + BuiltIn.class.getSimpleName()
                + " annotated field \"" + field.getName() + "\""
                + " in class " + field.getDeclaringClass().getName();

        if (!IoState.class.isAssignableFrom(field.getType())) {
            throw new IoStateException(fieldDesc
                    + " must be assignable from "
                    + IoState.class.getName());
        }

        int mods = field.getModifiers();
        if (!Modifier.isPublic(mods)) {
            throw new IoStateException(fieldDesc + " must be public");
        } else if (!Modifier.isFinal(mods)) {
            throw new IoStateException(fieldDesc + " must be final");
        } else if (Modifier.isStatic(mods)) {
            throw new IoStateException(fieldDesc + " cannot be static");
        }
    }

    @IoApi.Friends(IoDevice.class)
    @VisibleForTesting
    static void validateBuiltInFields(
            @NotNull Class<? extends IoDevice> clazz) {
        Set<Field> fields = new HashSet<>();
        Collections.addAll(fields, clazz.getDeclaredFields());
        Collections.addAll(fields, clazz.getFields());
        for (Field field : fields) {
            validateBuiltInField(field);
        }
    }

    /**
     * A value representing no internals for an I/O state.
     */
    protected static final @NotNull NoInternals
            NO_INTERNALS = NoInternals.INSTANCE;

    /**
     * The I/O device this state belongs to.
     * <p>
     * <b>Visibility:</b> This field has no associated public getter by
     * design. This allows the state to be passed without exposing its
     * device.
     */
    protected final @NotNull IoDevice device;

    /**
     * The I/O feature this state represents.
     *
     * @see #getFeature()
     */
    protected final @NotNull IoFeature<?> feature;

    /**
     * The internals of this I/O state.
     * <p>
     * <b>Visibility:</b> The value of this field is accessible to a variety
     * of other objects. Unless otherwise exposed, these are:
     * <ul>
     *     <li>The {@link IoDevice} this state belongs to.</li>
     *     <li>The {@link IoHandle} the device is bound to.</li>
     * </ul>
     *
     * @see IoDevice#getInternals(IoFeature)
     */
    @IoApi.Friends({IoDevice.class, IoHandle.class})
    protected final @NotNull I internals;

    /**
     * Constructs a new {@code IoState}.
     *
     * @param device    the I/O device this state belongs to.
     * @param feature   the I/O feature this state represents.
     * @param internals the internal data of this state.
     * @throws NullPointerException     if {@code device}, {@code feature}
     *                                  or {@code internals} are {@code null}.
     * @throws IllegalArgumentException if {@code internals} is an instance
     *                                  of an {@code IoState}.
     */
    public IoState(@NotNull IoDevice device, @NotNull IoFeature<?> feature,
                   @NotNull I internals) {
        Objects.requireNonNull(device, "device cannot be null");
        Objects.requireNonNull(feature, "feature cannot be null");
        Objects.requireNonNull(internals, "internals cannot be null");

        /*
         * There's technically no reason this cannot occur. However, this
         * feels very hacky. As such, don't allow it. If a legitimate use for
         * this arises in the future, this restriction will be removed.
         */
        if (internals instanceof IoState) {
            throw new IllegalArgumentException("internals cannot be an"
                    + " instance of " + IoState.class.getSimpleName());
        }

        this.feature = feature;
        this.device = device;
        this.internals = internals;
    }

    /**
     * Returns the I/O feature this state represents.
     *
     * @return the I/O feature this state represents.
     */
    public final @NotNull IoFeature<?> getFeature() {
        return this.feature;
    }

    /**
     * Performs pre-processing for the state.
     * <p>
     * This method is invoked before the adapter for this state bridges it.
     * If the state has no adapter, then this method will never be invoked.
     */
    @IoApi.DefaultBehavior("no-op")
    protected void preprocess() {
        /* default behavior is a no-op */
    }

    /**
     * Performs post-processing for the state.
     * <p>
     * This method is invoked after the adapter for this state bridges it.
     * If the state has no adapter, then this method will never be invoked.
     */
    @IoApi.DefaultBehavior("no-op")
    protected void postprocess() {
        /* default behavior is a no-op */
    }

    /**
     * Resets the I/O state.
     */
    protected abstract void reset();

}