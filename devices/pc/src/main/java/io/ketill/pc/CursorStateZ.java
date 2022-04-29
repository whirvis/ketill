package io.ketill.pc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class CursorStateZ {

    public boolean visible;
    public @Nullable Vector2fc requestedPos;
    public final @NotNull Vector2f currentPos;

    public CursorStateZ() {
        this.currentPos = new Vector2f();
    }

}
