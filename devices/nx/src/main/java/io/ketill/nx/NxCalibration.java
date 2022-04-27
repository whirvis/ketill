package io.ketill.nx;

import io.ketill.PlainIoFeature;
import org.jetbrains.annotations.NotNull;

/**
 * An I/O feature representing the calibration of an {@link NxProController}.
 */
public final class NxCalibration extends PlainIoFeature<NxCalibrationConfig> {

    /**
     * @param id the feature ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public NxCalibration(@NotNull String id) {
        super(id, NxCalibrationConfig::new);
    }

}
