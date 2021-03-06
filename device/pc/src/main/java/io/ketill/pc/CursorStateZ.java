package io.ketill.pc;

import io.ketill.AutonomousField;
import io.ketill.AutonomousState;
import io.ketill.IoDeviceObserver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.awt.*;

/**
 * Contains the state of a {@link MouseCursor}.
 *
 * @see #visible
 * @see #requestedPos
 */
public final class CursorStateZ implements AutonomousState {

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
            adapterCanSetPosition,
            adapterCanSetIcon;
    /* @formatter:on */

    /**
     * This should be updated by the adapter to store the current position
     * of the mouse cursor.
     */
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

    /**
     * This should <i>not</i> be modified by the adapter.<br>
     * It is updated automatically by the state.
     *
     * @see #updatedIcon
     */
    @AutonomousField
    public Image icon;

    /**
     * When this value is {@code true}, the adapter should update the
     * icon of the cursor to reflect the contents of {@link #icon}.
     * Afterwards, this value should be set back to {@code false}.
     */
    public boolean updatedIcon;

    private final MouseCursor cursor;
    private final Mouse mouse;
    private final IoDeviceObserver observer;

    private final Vector2f lastPos;
    private boolean wasVisible;
    boolean emitIconUpdated;

    CursorStateZ(@NotNull MouseCursor cursor,
                 @NotNull IoDeviceObserver observer) {
        this.cursor = cursor;
        this.mouse = (Mouse) observer.getDevice();
        this.observer = observer;

        this.currentPos = new Vector2f();
        this.visible = true;

        this.lastPos = new Vector2f();
        this.wasVisible = true;
    }

    @Override
    public void update() {
        if (!currentPos.equals(lastPos)) {
            Vector2f displacement = new Vector2f();
            currentPos.sub(lastPos, displacement);
            observer.onNext(new MouseCursorDisplaceEvent(mouse,
                    cursor, displacement));
        }
        lastPos.set(currentPos);

        if (wasVisible && !visible) {
            this.wasVisible = false;
            observer.onNext(new MouseCursorSetVisibilityEvent(mouse,
                    cursor, false));
        } else if (!wasVisible && visible) {
            this.wasVisible = true;
            observer.onNext(new MouseCursorSetVisibilityEvent(mouse,
                    cursor, true));
        }

        if (emitIconUpdated) {
            observer.onNext(new MouseCursorSetIconEvent(mouse, cursor, icon));
            this.emitIconUpdated = false;
        }
    }

}
