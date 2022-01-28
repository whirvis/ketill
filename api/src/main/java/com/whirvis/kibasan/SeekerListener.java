package com.whirvis.kibasan;

import org.jetbrains.annotations.NotNull;

public interface SeekerListener<I extends InputDevice> {

    /**
     * Called when an input device has been discovered.
     *
     * @param seeker     the seeker which discovered {@code device}.
     * @param discovered the device that was discovered.
     */
    void onDiscoverDevice(@NotNull DeviceSeeker<?> seeker,
                          @NotNull I discovered);

    /**
     * Called when an input device has been forgotten.
     *
     * @param seeker    the seeker which forgot {@code device}.
     * @param forgotten the device that was forgotten.
     */
    /* @formatter:off */
    void onForgetDevice(@NotNull DeviceSeeker<?> seeker,
                        @NotNull I forgotten);
    /* @formatter:on */

    /**
     * Called when a seeker error has occurred.
     *
     * @param seeker the seeker which had an error.
     * @param cause  the error that occurred.
     */
    void onError(@NotNull DeviceSeeker<?> seeker, @NotNull Throwable cause);

}
