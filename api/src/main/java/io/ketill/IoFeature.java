package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.*;
import java.util.Objects;

/**
 * The definition for an {@link IoDevice} capability.
 * <p>
 * Examples include (but are not limited to): a gamepad button, an analog
 * stick, a rumble motor, or an LED indicator. Each of these features has
 * a corresponding {@link IoState}.
 *
 * @param <S> the I/O state type.
 * @see BuiltIn
 * @see IoDevice#addFeature(IoFeature)
 * @see IoLogic
 */
public abstract class IoFeature<S extends IoState<?>> {

    /**
     * When present, indicates to an {@link IoDevice} that a field contains
     * an {@code IoFeature} which is part of it. Using this annotation also
     * ensures the field has proper form for an I/O feature declaration.
     * <p>
     * <b>Requirements</b>
     * <p>
     * <ul>
     *     <li>The field must be {@code public} and {@code final}.</li>
     *     <li>The type must assignable from {@code IoFeature}.</li>
     *     <li>The field must be {@code static}.</li>
     * </ul>
     * <p>
     * If these requirements are not met, an appropriate exception shall
     * be thrown by the constructor of {@code IoDevice}.
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

    static class Cache {

        final @NotNull IoFeature<?> feature;
        final @NotNull IoState<?> state;
        final @Nullable IoLogic<?> logic;

        Cache(@NotNull IoFeature<?> feature, @NotNull IoState<?> state,
              @Nullable IoLogic<?> logic) {
            this.feature = feature;
            this.state = state;
            this.logic = logic;
        }

    }

    /**
     * Checks that the specified I/O feature ID is valid, and throws an
     * exception if it is not. For the ID to be valid: it must not be
     * {@code null}, must not be empty, and cannot contain whitespace.
     *
     * @param id the ID to validate.
     * @return {@code id} if valid.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public static @NotNull String validateId(@NotNull String id) {
        Objects.requireNonNull(id, "id cannot be null");
        if (id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be empty");
        } else if (!id.matches("\\S+")) {
            throw new IllegalArgumentException("id cannot contain whitespace");
        }
        return id;
    }

    private final @NotNull String id;
    private final @NotNull IoFlow flow;
    private boolean modular;

    /**
     * Constructs a new {@code IoFeature}.
     *
     * @param id      the ID of this I/O feature.
     * @param flow    the flow of this I/O feature.
     * @param modular {@code true} if this I/O feature can be added and/or
     *                removed from the device after instantiation, {@code false}
     *                otherwise.
     * @throws NullPointerException     if {@code id} or {@code flow} are
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public IoFeature(@NotNull String id, @NotNull IoFlow flow,
                     boolean modular) {
        this.id = validateId(id);

        this.flow = Objects.requireNonNull(flow, "flow cannot be null");
        this.modular = modular;
    }

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
        this(id, flow, false);
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


    /**
     * Returns if this I/O feature is modular.
     * <p>
     * Modular I/O features are unique in that they can be removed after
     * they're added to a device. Some examples of modular I/O features
     * include (but are not limited to): the headset for an XBOX controller,
     * the buttons on the Pro Controller for the Wiimote, etc.
     *
     * @return {@code true} if this I/O feature is modular, {@code false}
     * otherwise.
     * @see IoDevice#removeFeature(IoFeature)
     */
    public final boolean isModular() {
        return this.modular;
    }

    /**
     * Creates a new instance of the I/O feature's state.
     * <p>
     * <b>Requirements</b>
     * <p>
     * <ul>
     *     <li>The returned value must not be {@code null}.</li>
     *     <li>The state must represent this feature. This means a call to
     *     {@link IoState#getFeature()} on the returned value must return
     *     this instance.</li>
     * </ul>
     * <p>
     * If these requirements are not met, an exception shall be thrown by
     * {@link IoDevice#addFeature(IoFeature)}.
     *
     * @return the newly created I/O feature state.
     * @see #createVerifiedState()
     * @see #createLogic(IoDevice, IoState)
     */
    protected abstract @NotNull S createState();

    /**
     * Wrapper for {@link #createState()}, which verifies the created
     * state meets the necessary requirements. If they are not met, an
     * exception shall be thrown.
     *
     * @return the newly created, verified I/O feature state.
     * @throws NullPointerException     if the created state is {@code null}.
     * @throws IllegalArgumentException if the created state is not represented
     *                                  by this feature.
     */
    protected final @NotNull S createVerifiedState() {
        S state = this.createState();
        Objects.requireNonNull(state, "created state cannot be null");
        if (state.getFeature() != this) {
            String msg = "created state must represent this feature";
            throw new IllegalArgumentException(msg);
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
     * @see #createState()
     */
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
                .add("modular=" + modular)
                .toString();
    }
    /* @formatter:on */

}
