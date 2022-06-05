package io.ketill.pc;

import io.ketill.IoFeatureEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Emitted by {@link Mouse} when a {@link MouseCursor} has updated its icon.
 */
public final class MouseCursorSetIconEvent extends IoFeatureEvent
        implements MouseCursorEvent {

    private final Image icon;

    MouseCursorSetIconEvent(@NotNull Mouse mouse,
                            @NotNull MouseCursor cursor,
                            @Nullable Image icon) {
        super(mouse, cursor);
        this.icon = icon;
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
     * Returns the new cursor icon.
     *
     * @return the new cursor icon, a value of {@code null} indicates that
     * the default icon is now in use.
     */
    public @Nullable Image getIcon() {
        return this.icon;
    }

}
