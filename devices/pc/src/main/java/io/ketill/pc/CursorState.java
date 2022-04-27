package io.ketill.pc;

import io.ketill.StateContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.Objects;

/**
 * Read-only view of a mouse's cursor.
 */
public class CursorState extends StateContainer<CursorStateZ> {

    /**
     * @param internalState the internal state.
     * @throws NullPointerException if {@code internalState} is {@code null}.
     */
    public CursorState(CursorStateZ internalState) {
        super(internalState);
    }

    /**
     * @return {@code true} if the mouse cursor is currently visible,
     * {@code false} otherwise.
     */
    public boolean isVisible() {
        return internalState.visible;
    }

    /**
     * @param visible {@code true} if the mouse cursor should be visible,
     *                {@code false} otherwise.
     */
    public void setVisible(boolean visible) {
        internalState.visible = visible;
    }

    /**
     * @return the position of the cursor.
     */
    public @NotNull Vector2fc getPosition() {
        return internalState.currentPos;
    }

    /**
     * @return the X-axis position of the cursor.
     * @see #getPosition()
     */
    public final float getX() {
        return this.getPosition().x();
    }

    /**
     * @return the Y-axis position of the cursor.
     * @see #getPosition()
     */
    public final float getY() {
        return this.getPosition().y();
    }

    /**
     * @param pos the position to set the cursor to.
     * @throws NullPointerException if {@code pos} is {@code null}.
     */
    public void setPosition(@NotNull Vector2fc pos) {
        Objects.requireNonNull(pos, "pos");
        internalState.requestedPos = pos;
    }

    /**
     * @param xPos the X-axis position to set the cursor to.
     * @param yPos the Y-axis position to set the cursor to.
     * @see #setPosition(Vector2fc)
     */
    public final void setPosition(float xPos, float yPos) {
        this.setPosition(new Vector2f(xPos, yPos));
    }

}
