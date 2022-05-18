package io.ketill;

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
 * @see AutonomousField
 * @see ContainerState
 */
public interface AutonomousState {

    /**
     * Updates the internal state <i>after</i> it has been updated by the
     * adapter of an I/O device.
     */
    void update();

}
