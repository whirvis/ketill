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
 * internals type. Use {@link #IoState(IoFeature, Class)} to get around
 * the restriction of not being able to reference {@code this} before
 * {@code super} has finished instantiation.
 *
 * @param <I> the internal data type.
 * @see BuiltIn
 * @see IoDevice#getState(IoFeature)
 * @see IoLogic
 */
public abstract class IoState<I> {

    /**
     * When present, indicates to an {@link IoDevice} that a field contains
     * the state of a built-in {@link IoFeature}. Using this annotation also
     * ensures the field has proper form for an I/O state declaration.
     * <p>
     * <b>Requirements</b>
     * <p>
     * <ul>
     *     <li>The field must be {@code public} and {@code final}.</li>
     *     <li>The type must be assignable from {@code IoState}.</li>
     *     <li>The field cannot be {@code static}.</li>
     * </ul>
     * <p>
     * If these requirements are not met, an appropriate exception shall
     * be thrown by the constructor of {@code IoDevice}.
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
     * The I/O feature this state represents.
     */
    protected final @NotNull IoFeature<?, ?> feature;

    /**
     * The internals of this I/O state.
     * <p>
     * <b>Visibility:</b> For purposes of implementation, the value of this
     * field is accessible to a variety of other objects. Unless otherwise
     * exposed, these are:
     * <ul>
     *     <li>The {@link IoLogic} for this state, if any.</li>
     *     <li>The {@link IoDevice} which owns the feature.</li>
     *     <li>The {@link IoAdapter} of the device.</li>
     * </ul>
     *
     * @see IoDevice#getInternals(IoFeature)
     */
    protected final @NotNull I internals;

    private final boolean mutableView;

    /**
     * Constructs a new {@code IoState}.
     *
     * @param feature   the I/O feature of this state.
     * @param internals the internal data of this state.
     * @throws NullPointerException     if {@code feature} or
     *                                  {@code internals} are {@code null}.
     * @throws IllegalArgumentException if {@code internals} is another
     *                                  instance of an {@code IoState}.
     */
    public IoState(@NotNull IoFeature<?, ?> feature, @NotNull I internals) {
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
        this.internals = internals;
        this.mutableView = false;
    }

    /**
     * Constructs a new {@code IoState} with no internals.
     * <p>
     * This constructor sets the {@code internals} field to {@code this}.
     * This gives the {@link IoAdapter}'s assigned state updater an object
     * it can read from and/or write to. Doing this also keeps the promise
     * that {@code internals} shall not have a {@code null} value.
     *
     * @param feature the I/O feature of this state.
     * @param type    the I/O state's type class.
     * @throws NullPointerException if {@code feature} or {@code type}
     *                              are {@code null}.
     * @throws ClassCastException   if {@code type} is not equal to this
     *                              class.
     */
    @SuppressWarnings("unchecked") /* we check ourselves */
    public IoState(@NotNull IoFeature<?, ?> feature, @NotNull Class<I> type) {
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

        this.feature = feature;
        this.internals = (I) this;
        this.mutableView = false;
    }

    /**
     * Constructs a mutable {@code IoState} wrapper.
     * <p>
     * The constructor assumes this state is a mutable view of another,
     * immutable state. As such, the given state must be a super class of
     * this type and cannot be a mutable view of another state.
     *
     * @param state the immutable I/O state.
     * @throws NullPointerException     if {@code state} is {@code null}.
     * @throws IllegalArgumentException if {@code state} is a mutable view
     *                                  of another state; if {@code state}
     *                                  is not a super class of this type.
     */
    public IoState(@NotNull IoState<I> state) {
        Objects.requireNonNull(state, "state cannot be null");

        /*
         * If the given state is a mutable view of another state, assume
         * it is a mistake by the user. Even if it isn't, it would likely
         * cause issues down the road.
         */
        if (state.mutableView) {
            String msg = "state is a mutable view of another state";
            throw new IllegalStateException(msg);
        }

        /*
         * TODO: explain this restriction
         */
        Class<?> clazz = this.getClass();
        if (!state.getClass().isAssignableFrom(clazz)) {
            String msg = ""; // TODO: error message
            throw new IllegalArgumentException(msg);
        }

        this.feature = state.getFeature();
        this.internals = state.internals;
        this.mutableView = true;
    }

    /**
     * Returns the I/O feature of this state.
     *
     * @return the I/O feature of this state.
     */
    public final @NotNull IoFeature<?, ?> getFeature() {
        return this.feature;
    }

    /**
     * Resets the I/O state.
     */
    protected abstract void reset();

}