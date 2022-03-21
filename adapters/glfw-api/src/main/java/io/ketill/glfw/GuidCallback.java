package io.ketill.glfw;

import org.jetbrains.annotations.NotNull;

/**
 * A callback for events relating to GUIDs in a {@link GlfwJoystickSeeker}.
 *
 * @see GlfwJoystickSeeker#onWrangleGuid(GuidCallback)
 * @see GlfwJoystickSeeker#onReleaseGuid(GuidCallback)
 */
@FunctionalInterface
public interface GuidCallback {

    void execute(@NotNull GlfwJoystickSeeker<?> seeker,
                 @NotNull String guid,
                 @NotNull GlfwJoystickWrangler<?> wrangler);

}
