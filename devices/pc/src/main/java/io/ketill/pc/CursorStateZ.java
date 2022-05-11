package io.ketill.pc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class CursorStateZ {

    /**
     * These fields indicate if an I/O device adapter has the ability to
     * perform a specific function for the mouse cursor. By default, it is
     * assumed an adapter cannot perform <i>any</i> of these. The adapter
     * must indicate they support a feature by setting their corresponding
     * field to {@code true}, ideally during initialization.
     */
    /* @formatter:off */
    public boolean
            adapterCanSetVisible,
            adapterCanSetPosition;
    /* @formatter:on */

    public boolean visible;
    public @Nullable Vector2fc requestedPos;
    public final @NotNull Vector2f currentPos;

    public CursorStateZ() {
        this.visible = true;
        this.currentPos = new Vector2f();
    }

}
