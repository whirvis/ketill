package com.whirvis.ketill.pc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class Cursor2f extends Vector2f {

    public boolean visible;
    private Vector2fc requestedPos;

    /**
     * @param visible the initial visibility.
     */
    public Cursor2f(boolean visible) {
        this.visible = visible;
    }

    /**
     * Constructs a new {@code Cursor2f} with visibility enabled.
     */
    public Cursor2f() {
        this(true);
    }

    /**
     * Calling this method clears the internal {@code requestedPos} field,
     * setting it to {@code null}. As such, only the adapter which fulfills the
     * duty of moving the cursor to the requested position should call this.
     *
     * @return the requested cursor position, {@code null} if none.
     */
    public @Nullable Vector2fc getRequestedPos() {
        Vector2fc pos = this.requestedPos;
        this.requestedPos = null;
        return pos;
    }

    public void setPosition(@NotNull Vector2fc pos) {
        this.requestedPos = pos;
    }

}
