package io.ketill.nx;

import io.ketill.AdapterSupplier;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.Controller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A Nintendo Switch Joy-Con.
 *
 * @see #asLeftJoyCon()
 * @see #asRightJoyCon()
 */
public abstract class NxJoyCon extends Controller {

    /**
     * This constructor is package-private so only this module can actually
     * extend the class. The class is left public so users can generalize
     * left and right Joy-Cons.
     *
     * @param id              the Joy-Con ID.
     * @param adapterSupplier the Joy-Con adapter supplier.
     * @param ls              the left analog stick, may be {@code null}.
     * @param rs              the right analog stick, may be {@code null}.
     * @param lt              the left analog trigger, may be {@code null}.
     * @param rt              the right analog trigger, may be {@code null}.
     * @throws NullPointerException     if {@code id} or
     *                                  {@code adapterSupplier}
     *                                  are {@code null}; if the adapter
     *                                  given by {@code adapterSupplier}
     *                                  is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    NxJoyCon(@NotNull String id,
             @NotNull AdapterSupplier<?> adapterSupplier,
             @Nullable AnalogStick ls, @Nullable AnalogStick rs,
             @Nullable AnalogTrigger lt, @Nullable AnalogTrigger rt) {
        super(id, adapterSupplier, ls, rs, lt, rt);
    }

    /**
     * @return {@code true} if this a left Joy-Con, {@code false} otherwise.
     * @see #asLeftJoyCon()
     */
    public final boolean isLeftJoyCon() {
        return this instanceof NxLeftJoyCon;
    }

    /**
     * @return this instance as an {@link NxLeftJoyCon} instance.
     * @throws UnsupportedOperationException if this is not a left Joy-Con.
     * @see #isLeftJoyCon()
     */
    public final @NotNull NxLeftJoyCon asLeftJoyCon() {
        if (!this.isLeftJoyCon()) {
            throw new UnsupportedOperationException("not a left JoyCon");
        }
        return (NxLeftJoyCon) this;
    }

    /**
     * @return {@code true} if this a right Joy-Con, {@code false} otherwise.
     * @see #asRightJoyCon()
     */
    public final boolean isRightJoyCon() {
        return this instanceof NxRightJoyCon;
    }

    /**
     * @return this instance as an {@link NxRightJoyCon} instance.
     * @throws UnsupportedOperationException if this is not a right Joy-Con.
     * @see #isRightJoyCon()
     */
    public final @NotNull NxRightJoyCon asRightJoyCon() {
        if (!this.isRightJoyCon()) {
            throw new UnsupportedOperationException("not a right JoyCon");
        }
        return (NxRightJoyCon) this;
    }

}
