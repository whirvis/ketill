package io.ketill;

import io.ketill.handle.IoHandle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The definition for an {@link IoDevice} capability.
 * <p>
 * Examples include (but are not limited to): a button, an analog stick,
 * a rumble motor, or an LED. Each of these features have a corresponding
 * {@link IoState}.
 *
 * @param <S> the I/O state type.
 * @see BuiltIn
 * @see IoDevice#addFeature(IoFeature)
 */
public abstract class IoFeature<S extends IoState<?>> {

    /**
     * When present, indicates to an {@link IoDevice} that a field contains
     * an {@code IoFeature} which is part of it. Using this annotation also
     * ensures the field has proper form for an I/O feature declaration.
     * <p>
     * <b>Requirements</b>
     * <p>
     * This annotation requires that:
     * <ul>
     *     <li>The field must be {@code public} and {@code final}.</li>
     *     <li>The type must assignable from {@code IoFeature}.</li>
     *     <li>The field must be {@code static}.</li>
     * </ul>
     * <p>
     * If these requirements are not met, an appropriate exception shall
     * be thrown by the constructor of {@code IoDevice}. In addition, they
     * must not be {@code null}. However, the order of class instantiation
     * prevents this from being enforced at runtime.
     * <p>
     * <b>Recommendations</b>
     * <p>
     * The recommended naming convention for these fields is upper snake
     * case, with the name beginning with an abbreviation of the feature
     * type. This is to clearly distinguish them from their sister fields,
     * which contain the state for a specific {@code IoDevice} instance.
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
     * @see IoDevice#addFeature(IoFeature)
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

        if (!IoFeature.class.isAssignableFrom(field.getType())) {
            throw new IoFeatureException(fieldDesc
                    + " must be assignable from "
                    + IoFeature.class.getName());
        }

        int mods = field.getModifiers();
        if (!Modifier.isPublic(mods)) {
            throw new IoFeatureException(fieldDesc + " must be public");
        } else if (!Modifier.isFinal(mods)) {
            throw new IoFeatureException(fieldDesc + " must be final");
        } else if (!Modifier.isStatic(mods)) {
            throw new IoFeatureException(fieldDesc + " must be static");
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

    private final @NotNull String id;

    /**
     * Constructs a new {@code IoFeature}.
     *
     * @param id the ID of this I/O feature.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    IoFeature(@NotNull String id) {
        this.id = IoApi.validateId(id);
    }

    /**
     * Returns the ID of this I/O feature.
     *
     * @return the ID of this I/O feature.
     */
    public final @NotNull String getId() {
        return this.id;
    }

    /* TODO: configs here */

    /**
     * Creates a new instance of the I/O feature's state.
     * <p>
     * <b>Requirements</b>
     * <ul>
     *     <li>The returned value must not be {@code null}.</li>
     *     <li>The state must be owned by {@code device}.</li>
     *     <li>The state must represent this feature.</li>
     * </ul>
     *
     * @param device the I/O device which the state belongs to.
     * @return the newly created I/O state.
     */
    protected abstract @NotNull S createState(@NotNull IoDevice device);

    @Override
    public String toString() {
        return IoApi.getStrJoiner(this)
                .add("id=\"" + id + "\"")
                .toString();
    }

}