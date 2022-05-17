package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * An interface that, when implemented by the internal state of an
 * {@link IoFeature}, gives it autonomy.
 * <p>
 * When implemented, this should be used to update the internal state
 * of a feature where the adapter is not considered responsible (e.g.,
 * the calibration of an analog stick).
 * <p>
 * <b>Note:</b> This interface can be implemented by the internal state
 * only. When implemented by the container state, it will result in an
 * {@code UnsupportedOperationException} during creation.
 *
 * @see ContainerState
 */
public interface AutonomousState {

    /**
     * Updates the internal state <i>after</i> it has been updated by the
     * adapter of an I/O device.
     *
     * @param feature the feature which created this state.
     * @param events  an observer which can emit events to subscribers of
     *                the I/O device which owns this state.
     */
    void update(@NotNull IoFeature<?, ?> feature,
                @NotNull IoDeviceObserver events);

}
