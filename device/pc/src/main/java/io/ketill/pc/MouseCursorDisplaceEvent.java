package io.ketill.pc;

import io.ketill.IoFeatureEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2fc;

/**
 * Emitted by {@link Mouse} when a {@link MouseCursor} has moved position.
 */
public final class MouseCursorDisplaceEvent extends IoFeatureEvent
        implements MouseCursorEvent {

    private final Vector2fc displacement;

    MouseCursorDisplaceEvent(@NotNull Mouse mouse,
                             @NotNull MouseCursor cursor,
                             @NotNull Vector2fc displacement) {
        super(mouse, cursor);
        this.displacement = displacement;
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
     * Returns how many pixels the cursor moved.
     *
     * @return how many pixels the cursor moved.
     */
    public @NotNull Vector2fc getDisplacement() {
        return this.displacement;
    }

}
