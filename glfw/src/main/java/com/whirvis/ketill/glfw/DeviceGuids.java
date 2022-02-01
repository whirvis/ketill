package com.whirvis.ketill.glfw;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DeviceGuids {

    /**
     * @param id the input device ID.
     * @param os the operating system ID.
     * @return the GUIDs for device with {@code id} when running on {@code os},
     * {@code null} if no such set of GUIDs exists.
     */
    @Nullable Iterable<@NotNull String> getGuids(@NotNull String id,
                                                 @NotNull String os);

    /**
     * @param id the input device ID.
     * @param os the operating system.
     * @return the GUIDs for device with {@code id} when running on {@code os},
     * {@code null} if no such set of GUIDs exists.
     */
    /* @formatter:off */
    default @Nullable Iterable<@NotNull String>
            getGuids(@NotNull String id, @NotNull OperatingSystem os) {
        return this.getGuids(id, os.id);
    }
    /* @formatter:on */

}
