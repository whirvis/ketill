package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * The definition for an {@link IoDevice} capability.
 * <p>
 * Examples include (but are not limited to): a gamepad button, an analog
 * stick, a rumble motor, or an LED indicator. Each of these features has
 * a corresponding {@link IoState}.
 *
 * @param <S> the I/O state type.
 * @see BuiltIn
 * @see #createState(IoDevice)
 * @see #createLogic(IoDevice, IoState)
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

    private static void validateBuiltInField(@NotNull Field field) {
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
    static void validateBuiltInFields(
            @NotNull Class<? extends IoDevice> clazz) {
        Set<Field> fields = new HashSet<>();
        Collections.addAll(fields, clazz.getDeclaredFields());
        Collections.addAll(fields, clazz.getFields());
        for (Field field : fields) {
            validateBuiltInField(field);
        }
    }

    @IoApi.Friends({IoDevice.class, IoAdapter.class})
    static class Cache {

        private static final Consumer<IoFlow> NO_OP = (f) -> {
            /* do nothing by default */
        };

        final @NotNull IoFeature<?> feature;
        final @NotNull IoState<?> state;
        final @Nullable IoLogic<?> logic;

        final @NotNull Consumer<IoFlow> logicPreprocess;
        final @NotNull Consumer<IoFlow> logicPostprocess;

        Cache(@NotNull IoFeature<?> feature, @NotNull IoState<?> state,
              @Nullable IoLogic<?> logic) {
            this.feature = feature;
            this.state = state;
            this.logic = logic;

            if (logic == null) {
                this.logicPreprocess = NO_OP;
                this.logicPostprocess = NO_OP;
            } else {
                this.logicPreprocess = logic::preprocess;
                this.logicPostprocess = logic::postprocess;
            }
        }

    }

    private final @NotNull String id;
    private final @NotNull IoFlow flow;

    /**
     * Constructs a new {@code IoFeature}.
     *
     * @param id   the ID of this I/O feature.
     * @param flow the flow of this I/O feature.
     * @throws NullPointerException     if {@code id} or {@code flow} are
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public IoFeature(@NotNull String id, @NotNull IoFlow flow) {
        this.id = IoApi.validateId(id);
        this.flow = Objects.requireNonNull(flow,
                "flow cannot be null");
    }

    /**
     * Returns the ID of this I/O feature.
     *
     * @return the ID of this I/O feature.
     */
    public final @NotNull String getId() {
        return this.id;
    }

    /**
     * Returns the flow of this I/O feature.
     *
     * @return the flow of this I/O feature.
     */
    public final @NotNull IoFlow getFlow() {
        return this.flow;
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
     * <p>
     * If the above requirements are not met, an exception shall be thrown
     * by {@link #createVerifiedState(IoDevice)}.
     *
     * @param device the I/O device which {@code state} belongs to.
     * @return the newly created I/O state.
     * @see #createLogic(IoDevice, IoState)
     */
    protected abstract @NotNull S createState(@NotNull IoDevice device);

    /**
     * Wrapper for {@link #createState(IoDevice)}, which verifies the
     * created state meets the necessary requirements. If they are not
     * met, an {@code IoFeatureException} shall be thrown.
     *
     * @param device the I/O device which {@code state} belongs to.
     * @return the newly created, verified I/O feature state.
     * @throws IoFeatureException if the created state is {@code null};
     *                            if the created state is not represented
     *                            by this feature.
     */
    @SuppressWarnings("ConstantConditions")
    protected final @NotNull S createVerifiedState(@NotNull IoDevice device) {
        Objects.requireNonNull(device, "device cannot be null");
        S state = this.createState(device);

        if (state == null) {
            String msg = "created state cannot be null";
            throw new IoFeatureException(msg);
        } else if (state.device != device) {
            String msg = "created state not owned by provided device";
            throw new IllegalArgumentException(msg);
        } else if (state.feature != this) {
            String msg = "created state must represent this feature";
            throw new IoFeatureException(msg);
        }

        return state;
    }

    /**
     * Creates a new instance of the I/O feature's logic.
     * <p>
     * <b>Requirements</b>
     * <p>
     * <ul>
     *     <li>The logic must be owned by {@code device}.</li>
     *     <li>The logic must manage {@code state}.</li>
     * </ul>
     * <p>
     * If these requirements are not met, an exception shall be thrown by
     * {@link IoDevice#addFeature(IoFeature)}. Take note that these only
     * apply to not {@code null} return values.
     *
     * @param device the I/O device which {@code state} belongs to.
     * @param state  the I/O state the logic will manage.
     * @return the newly created I/O logic, {@code null} if no logic
     * instance shall manage the state.
     * @see #createState(IoDevice)
     */
    @SuppressWarnings("unused")
    @IoApi.DefaultBehavior("return null")
    protected @Nullable IoLogic<?>
    createLogic(@NotNull IoDevice device, @NotNull S state) {
        return null; /* no logic by default */
    }

    /**
     * Wrapper for {@link #createLogic(IoDevice, IoState)}, which verifies
     * the created logic for a given state meets the necessary requirements.
     * If they are not met, an exception shall be thrown.
     *
     * @return the newly created, verified I/O feature logic.
     * @throws NullPointerException     if {@code device} or {@code state}
     *                                  are {@code null}.
     * @throws IllegalArgumentException if the created logic is not owned
     *                                  by {@code device}, does not manage
     *                                  {@code state}, or is not represented
     *                                  by this feature.
     */
    protected @Nullable IoLogic<?>
    createVerifiedLogic(@NotNull IoDevice device, @NotNull S state) {
        Objects.requireNonNull(device, "device cannot be null");
        Objects.requireNonNull(state, "state cannot be null");

        IoLogic<?> logic = this.createLogic(device, state);
        if (logic == null) {
            return null;
        }

        if (logic.device != device) {
            String msg = "created logic not owned by provided device";
            throw new IllegalArgumentException(msg);
        } else if (logic.state != state) {
            String msg = "created logic must manage provided state";
            throw new IllegalArgumentException(msg);
        } else if (logic.feature != this) {
            String msg = "created logic must represent this feature";
            throw new IllegalArgumentException(msg);
        }

        return logic;
    }

    /* @formatter:off */
    @Override
    public String toString() {
        return IoApi.getStrJoiner(this)
                .add("id='" + id + "'")
                .add("flow=" + flow)
                .toString();
    }
    /* @formatter:on */

}