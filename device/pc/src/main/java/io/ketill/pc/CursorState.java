package io.ketill.pc;

import io.ketill.ContainerState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.awt.*;
import java.util.Objects;

/**
 * Read-only view of a mouse's cursor.
 */
public final class CursorState extends ContainerState<CursorStateZ> {

    CursorState(@NotNull CursorStateZ internalState) {
        super(internalState);
    }

    /**
     * Returns if the adapter has the ability to update the cursor's
     * visibility.
     *
     * @return {@code true} if the adapter has the ability to update the
     * cursor's visibility, {@code false} otherwise.
     */
    public boolean canSetVisible() {
        return internalState.adapterCanSetVisible;
    }

    /**
     * Returns if the adapter has the ability to update the cursor's
     * current position.
     *
     * @return {@code true} if the adapter has the ability to update the
     * cursor's current position, {@code false} otherwise.
     */
    public boolean canSetPosition() {
        return internalState.adapterCanSetPosition;
    }

    /**
     * Returns if the adapter has the ability to update the cursor's
     * current icon.
     *
     * @return {@code true} if the adapter has the ability to update the
     * cursor's current icon, {@code false} otherwise.
     */
    public boolean canSetIcon() {
        return internalState.adapterCanSetIcon;
    }

    /**
     * Returns if the cursor is currently visible.
     *
     * @return {@code true} if the cursor is currently visible,
     * {@code false} otherwise.
     */
    public boolean isVisible() {
        return internalState.visible;
    }

    /**
     * Sets the cursor's visibility.
     *
     * @param visible {@code true} if the cursor should be visible,
     *                {@code false} otherwise.
     * @throws UnsupportedOperationException if the internal state of this
     *                                       cursor indicates that the adapter
     *                                       does not have the ability to set
     *                                       the cursor's visibility.
     * @see #canSetVisible()
     * @see #trySetVisible(boolean)
     */
    public void setVisible(boolean visible) {
        if (!this.canSetVisible()) {
            String msg = "cannot set visibility with current adapter";
            msg += ", did it forget to set adapterCanSetVisible";
            msg += " to true in the internal state?";
            throw new UnsupportedOperationException(msg);
        }
        internalState.visible = visible;
    }

    /**
     * Attempts to set the cursor's visibility.
     * <p>
     * <b>Alternative to:</b> {@link #setVisible(boolean)}, which only sets
     * the cursor's visibility if the adapter has the ability to do so.
     *
     * @param visible {@code true} if the cursor should be visible,
     *                {@code false} otherwise.
     * @return {@code true} if the cursor visibility was successfully
     * updated, {@code false} otherwise.
     */
    public boolean trySetVisible(boolean visible) {
        if (!this.canSetVisible()) {
            return false;
        }
        this.setVisible(visible);
        return true;
    }

    /**
     * Returns the position of the cursor.
     *
     * @return the position of the cursor.
     */
    public @NotNull Vector2fc getPosition() {
        return internalState.currentPos;
    }

    /**
     * Returns the X-axis position of the cursor.
     * <p>
     * <b>Shorthand for:</b> {@code getPosition().x()}
     *
     * @return the X-axis position of the cursor.
     * @see #getPosition()
     */
    public float getX() {
        return this.getPosition().x();
    }

    /**
     * Returns the Y-axis position of the cursor.
     * <p>
     * <b>Shorthand for:</b> {@code getPosition().y()}
     *
     * @return the Y-axis position of the cursor.
     * @see #getPosition()
     */
    public float getY() {
        return this.getPosition().y();
    }

    /**
     * Sets the cursor's position.
     *
     * @param pos the position to set the cursor to.
     * @throws NullPointerException          if {@code pos} is {@code null}.
     * @throws UnsupportedOperationException if the internal state of this
     *                                       cursor indicates that the adapter
     *                                       does not have the ability to set
     *                                       the cursor's current position.
     * @see #canSetPosition()
     * @see #trySetPosition(Vector2fc)
     */
    public void setPosition(@NotNull Vector2fc pos) {
        Objects.requireNonNull(pos, "pos cannot be null");
        if (!this.canSetPosition()) {
            String msg = "cannot set position with current adapter";
            msg += ", did it forget to set adapterCanSetPosition";
            msg += " to true in the internal state?";
            throw new UnsupportedOperationException(msg);
        }
        internalState.requestedPos = pos;
    }

    /**
     * Attempts to set the cursor's position.
     * <p>
     * <b>Alternative to:</b> {@link #setPosition(Vector2fc)}, which only
     * sets the cursor's current position if the adapter has the ability to
     * do so.
     *
     * @param pos the position to set the cursor to.
     * @return {@code true} if the cursor position was successfully updated,
     * {@code false} otherwise.
     * @throws NullPointerException if {@code pos} is {@code null}.
     */
    public boolean trySetPosition(@NotNull Vector2fc pos) {
        Objects.requireNonNull(pos, "pos cannot be null");
        if (!this.canSetPosition()) {
            return false;
        }
        this.setPosition(pos);
        return true;
    }

    /**
     * Sets the cursor's position.
     *
     * @param xPos the X-axis position to set the cursor to.
     * @param yPos the Y-axis position to set the cursor to.
     * @throws UnsupportedOperationException if the internal state of this
     *                                       cursor indicates that the adapter
     *                                       does not have the ability to set
     *                                       the cursor's current position.
     * @see #canSetPosition()
     * @see #trySetPosition(float, float)
     */
    public void setPosition(float xPos, float yPos) {
        this.setPosition(new Vector2f(xPos, yPos));
    }

    /**
     * Attempts to set the cursor's position.
     * <p>
     * <b>Alternative to:</b> {@link #setPosition(float, float)}, which only
     * sets the cursor's current position if the adapter has the ability to
     * do so.
     *
     * @param xPos the X-axis position to set the cursor to.
     * @param yPos the Y-axis position to set the cursor to.
     * @return {@code true} if the cursor position was successfully updated,
     * {@code false} otherwise.
     */
    public boolean trySetPosition(float xPos, float yPos) {
        return this.trySetPosition(new Vector2f(xPos, yPos));
    }

    /**
     * Returns the current icon of the cursor.
     *
     * @return the current icon of the cursor. A value of {@code null}
     * indicates that the default icon is currently in use.
     */
    public @Nullable Image getIcon() {
        return internalState.icon;
    }

    /**
     * Sets the cursor's icon.
     * <p>
     * <b>Note:</b> Only the current contents of {@code icon} will be used.
     * If its contents are updated afterwards, this method must be called
     * again for the changes to be reflected.
     *
     * @param icon the image to use for the cursor icon. A value of
     *             {@code null} is permitted, and indicates the default
     *             cursor should be used.
     * @throws UnsupportedOperationException if the internal state of this
     *                                       cursor indicates that the adapter
     *                                       does not have the ability to set
     *                                       the cursor's icon.
     * @see #canSetIcon()
     * @see #trySetIcon(Image)
     */
    public void setIcon(@Nullable Image icon) {
        if (!this.canSetIcon()) {
            String msg = "cannot set icon with current adapter";
            msg += ", did it forget to set adapterCanSetIcon";
            msg += " to true in the internal state?";
            throw new UnsupportedOperationException(msg);
        }
        internalState.icon = icon;
        internalState.updatedIcon = true;
        internalState.emitIconUpdated = true;
    }

    /**
     * Attempts to set the cursor's icon.
     * <p>
     * <b>Note:</b> Only the current contents of {@code icon} will be used.
     * If its contents are updated afterwards, this method must be called
     * again for the changes to be reflected.
     * <p>
     * <b>Alternative to:</b> {@link #setIcon(Image)}, which only sets the
     * cursor's icon if the adapter has the ability to do so.
     *
     * @param icon the image to use for the cursor icon. A value of
     *             {@code null} is permitted, and indicates the default
     *             cursor should be used.
     * @return {@code true} if the cursor icon was successfully updated,
     * {@code false} otherwise.
     */
    public boolean trySetIcon(@Nullable Image icon) {
        if (!this.canSetIcon()) {
            return false;
        }
        this.setIcon(icon);
        return true;
    }

}
