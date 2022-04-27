package io.ketill.pc;

import org.joml.Vector2f;
import org.joml.Vector2fc;

public class CursorStateZ {

    public boolean visible;
    public Vector2fc requestedPos;
    public final Vector2f currentPos;

    public CursorStateZ() {
        this.currentPos = new Vector2f();
    }

}
