package io.ketill.pc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * Contains the state of a {@link MouseCursor}.
 * <p>
 * <b>Note:</b> This class can be extended to implement extra cursor
 * functionality. However, if this is done, {@link MouseCursor} cannot
 * be used. A new I/O feature type will be needed to instantiate it.
 *
 * @see #visible
 * @see #requestedPos
 */
public class CursorStateZ {

    /**
     * These indicate if an I/O device adapter has the ability to perform a
     * specific function for the mouse cursor. By default, it is assumed an
     * adapter cannot perform <i>any</i> of these. The adapter must indicate
     * they support these by setting their value to {@code true}, ideally
     * during initialization.
     */
    /* @formatter:off */
    public boolean
            adapterCanSetVisible,
            adapterCanSetPosition;
    /* @formatter:on */

    public final @NotNull Vector2f currentPos;

    /**
     * When this value changes, the adapter should update the visibility of
     * the mouse cursor.
     *
     * @see #adapterCanSetVisible
     */
    public boolean visible;

    /**
     * When this value is not {@code null}, the adapter should move mouse
     * cursor to this position. Afterwards, this value should be set back
     * to {@code null}.
     *
     * @see #adapterCanSetPosition
     */
    public @Nullable Vector2fc requestedPos;

    public CursorStateZ() {
        this.currentPos = new Vector2f();
        this.visible = true;
    }

}
