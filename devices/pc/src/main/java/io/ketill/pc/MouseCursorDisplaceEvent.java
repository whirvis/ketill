package io.ketill.pc;

import io.ketill.IoFeatureEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2fc;

import java.util.Objects;

/**
 * Emitted by {@link Mouse} when a {@link MouseCursor} has moved position.
 */
public final class MouseCursorDisplaceEvent extends IoFeatureEvent
        implements MouseCursorEvent {

    private final Vector2fc displacement;

    /**
     * @param mouse  the mouse which emitted this event.
     * @param cursor the cursor which triggered this event.
     * @throws NullPointerException if {@code device} or {@code cursor}
     *                              are{@code null}.
     */
    public MouseCursorDisplaceEvent(@NotNull Mouse mouse,
                                    @NotNull MouseCursor cursor,
                                    @NotNull Vector2fc displacement) {
        super(mouse, cursor);
        this.displacement = Objects.requireNonNull(displacement,
                "displacement cannot be null");
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
     * @return how many pixels the cursor moved.
     */
    public @NotNull Vector2fc getDisplacement() {
        return this.displacement;
    }

}
