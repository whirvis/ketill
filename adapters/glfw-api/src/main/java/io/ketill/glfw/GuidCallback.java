package io.ketill.glfw;

import org.jetbrains.annotations.NotNull;

/**
 * A callback for events relating to GUIDs in a {@link GlfwJoystickSeeker}.
 *
 * @see GlfwJoystickSeeker#onSeekGuid(GuidCallback)
 * @see GlfwJoystickSeeker#onDropGuid(GuidCallback)
 */
@FunctionalInterface
public interface GuidCallback {

    void execute(@NotNull String guid,
                 @NotNull GlfwJoystickWrangler<?> wrangler);

}
