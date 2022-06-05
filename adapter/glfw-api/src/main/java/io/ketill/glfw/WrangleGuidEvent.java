package io.ketill.glfw;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Emitted by {@link GlfwJoystickSeeker} when a GUID is wrangled.
 */
public final class WrangleGuidEvent extends GlfwJoystickSeekerEvent {

    private final String guid;
    private final GlfwJoystickWrangler<?> wrangler;

    WrangleGuidEvent(@NotNull GlfwJoystickSeeker<?> seeker,
                     @NotNull String guid,
                     @NotNull GlfwJoystickWrangler<?> wrangler) {
        super(seeker);
        this.guid = Objects.requireNonNull(guid,
                "guid cannot be null");
        this.wrangler = Objects.requireNonNull(wrangler,
                "wrangler cannot be null");
    }

    /**
     * Returns the wrangled GUID.
     *
     * @return the wrangled GUID.
     */
    public @NotNull String getGuid() {
        return this.guid;
    }

    /**
     * Returns the wrangler assigned to the GUID.
     *
     * @return the wrangler assigned to the GUID.
     */
    public @NotNull GlfwJoystickWrangler<?> getWrangler() {
        return this.wrangler;
    }

}
