package io.ketill;

import org.jetbrains.annotations.NotNull;

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
 * Examples include (but are not limited to): if a gamepad button is
 * pressed, the position of an analog stick, the intensity of a rumble
 * motor, or the status of an LED.
 * <p>
 * <b>For I/O states with no internals:</b> use the state itself as the
 * internals type. Use {@link #IoState(IoDevice, IoFeature, Class)} to
 * get around the restriction of not being able to reference {@code this}
 * before {@code super} has finished instantiation.
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

    private static void validateBuiltInField(@NotNull Field field) {
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
     * The I/O device this state belongs to.
     * <p>
     * <b>Visibility:</b> This field has no associated public getter by
     * design. This allows for a state to be passed without exposing its
     * device.
     */
    protected final @NotNull IoDevice device;

    /**
     * The I/O feature this state represents.
     */
    protected final @NotNull IoFeature<?> feature;

    /**
     * The internals of this I/O state.
     * <p>
     * <b>Visibility:</b> For purposes of implementation, the value of this
     * field is accessible to a variety of other objects. Unless otherwise
     * exposed, these are:
     * <ul>
     *     <li>The {@link IoDevice} this state belongs to.</li>
     *     <li>The {@link IoHandle} of the device.</li>
     * </ul>
     *
     * @see IoDevice#getInternals(IoFeature)
     */
    protected final @NotNull I internals;

    /**
     * Constructs a new {@code IoState}.
     *
     * @param device    the I/O device this state belongs to.
     * @param feature   the I/O feature this state represents.
     * @param internals the internal data of this state.
     * @throws NullPointerException     if {@code device}, {@code feature}
     *                                  or {@code internals} are {@code null}.
     * @throws IllegalArgumentException if {@code internals} is another
     *                                  instance of an {@code IoState}.
     */
    public IoState(@NotNull IoDevice device, @NotNull IoFeature<?> feature,
                   @NotNull I internals) {
        Objects.requireNonNull(device, "device cannot be null");
        Objects.requireNonNull(feature, "feature cannot be null");
        Objects.requireNonNull(internals, "internals cannot be null");

        /*
         * There's technically no reason this can't occur. However, this
         * is a misuse of the IoState system. As such, do not allow it.
         * If a use for this arises in the future, the restriction will
         * be removed accordingly.
         */
        if (internals instanceof IoState) {
            String msg = "internals cannot be another";
            msg += " instance of " + IoState.class.getSimpleName();
            throw new IllegalArgumentException(msg);
        }

        this.feature = feature;
        this.device = device;
        this.internals = internals;
    }

    /**
     * Constructs a new {@code IoState} with no internals.
     * <p>
     * This constructor sets the {@code internals} field to {@code this}.
     * This gives the {@link IoHandle}'s assigned state updater an object
     * it can read from and/or write to. Doing this also keeps the promise
     * that {@code internals} shall not have a {@code null} value.
     *
     * @param device  the I/O device this state belong sto.
     * @param feature the I/O feature this state represents.
     * @param type    the I/O state's type class.
     * @throws NullPointerException if {@code device}, {@code feature}
     *                              or {@code type} are {@code null}.
     * @throws ClassCastException   if {@code type} is not equal to this
     *                              class.
     */
    @SuppressWarnings("unchecked") /* we check ourselves */
    public IoState(@NotNull IoDevice device, @NotNull IoFeature<?> feature,
                   @NotNull Class<I> type) {
        Objects.requireNonNull(device, "device cannot be null");
        Objects.requireNonNull(feature, "feature cannot be null");
        Objects.requireNonNull(type, "type cannot be null");

        /*
         * Before going any further, we must make sure that the generic
         * type provided by the user was actually this class. If it was
         * not, then the caller is attempting to construct an I/O state
         * with no internals under invalid circumstances.
         *
         * This is considered invalid as in the case there happen to be
         * no internals for a state, the state simply uses itself as the
         * internals so the state updater has something to access. This
         * will result in a ClassCastException occurring later down the
         * line. The exception will not occur here (even though it should
         * from intuition), due to how generics work in Java.
         */
        if (this.getClass() != type) {
            String msg = "type must be " + this.getClass().getName();
            throw new ClassCastException(msg);
        }

        this.device = device;
        this.feature = feature;
        this.internals = (I) this;
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
     * Returns if the feature this state represents is enabled.
     * <p>
     * A feature is considered enabled if the adapter responsible for this
     * state linked in the mode required to utilize it.
     * <p>
     * <b>Note:</b> An enabled feature <i>does not</i> indicate an active
     * feature. For a feature to be active, it must be enabled and supported.
     * It is also possible for an enabled feature to be unsupported.
     *
     * @return {@code true} if the feature this state represents is enabled,
     * {@code false} otherwise.
     * @see #isActive()
     */
    public final boolean isEnabled() {
        return device.isFeatureEnabled(feature);
    }

    /**
     * Returns if the feature this state represents is supported.
     * <p>
     * A feature is considered supported if the adapter responsible for this
     * state supports it (i.e., it has linked the feature).
     * <p>
     * <b>Note:</b> A supported feature <i>does not</i> indicate an active
     * feature. For a feature to be active, it must be enabled and supported.
     * It is also possible for a supported feature to be disabled.
     *
     * @return {@code true} if the feature this state represents is supported,
     * {@code false} otherwise.
     * @see #isActive()
     */
    public final boolean isSupported() {
        return device.isFeatureSupported(feature);
    }

    /**
     * Returns if the feature this state represents is active.
     * <p>
     * A feature is considered active if it is enabled and supported.
     *
     * @return {@code true} if the feature this state represents is active,
     * {@code false} otherwise.
     * @see #isEnabled()
     * @see #isSupported()
     */
    public final boolean isActive() {
        return device.isFeatureActive(feature);
    }

    /**
     * Prepares this state for a logical update.
     * <p>
     * This is invoked just before the adapter responsible for this state
     * updates it.
     * <p>
     * <b>Default behavior:</b> No-op.
     *
     * @param flow the current flow being updated.
     */
    @IoApi.DefaultBehavior("no-op")
    protected void preprocess(@NotNull IoFlow flow) {
        /* default behavior is a no-op */
    }

    /**
     * Executes a logical update.
     * <p>
     * This is invoked just after the adapter responsible for this state
     * updates it.
     * <p>
     * <b>Default behavior:</b> No-op.
     *
     * @param flow the current flow being updated.
     */
    @IoApi.DefaultBehavior("no-op")
    protected void postprocess(@NotNull IoFlow flow) {
        /* default behavior is a no-op */
    }

    /**
     * Resets the I/O state.
     */
    protected abstract void reset();

}