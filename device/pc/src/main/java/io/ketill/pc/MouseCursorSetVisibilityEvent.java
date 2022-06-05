package io.ketill.pc;

import io.ketill.IoFeatureEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Mouse} when a {@link MouseCursor} has updated its
 * visibility.
 */
public final class MouseCursorSetVisibilityEvent extends IoFeatureEvent
        implements MouseCursorEvent {

    private final boolean visible;

    MouseCursorSetVisibilityEvent(@NotNull Mouse mouse,
                                  @NotNull MouseCursor cursor,
                                  boolean visible) {
        super(mouse, cursor);
        this.visible = visible;
    }

    @Override
    public @NotNull Mouse getMouse() {
        return (Mouse) this.getDevice();
    }

    @Override
    public @NotNull MouseCursor getCursor() {
        return (MouseCursor) this.getFeature();
    }

    /**
     * Returns if the cursor is now visible.
     *
     * @return {@code true} if the cursor is now visible, {@code false}
     * if it is now invisible.
     */
    public boolean isVisible() {
        return this.visible;
    }

}
