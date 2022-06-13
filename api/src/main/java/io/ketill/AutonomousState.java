package io.ketill;

/**
 * Gives autonomy to the internal state of an {@link IoFeature}.
 * <p>
 * When implemented, this should be used to update the internal
 * state of a feature where the adapter should not be considered
 * responsible. For example, the calibration of an analog stick
 * or a controller button emitting press events.
 * <p>
 * <b>Requirements:</b> This interface can only be implemented by
 * the internal state. When implemented by the container state, it
 * will result in an {@code UnsupportedOperationException}.
 *
 * @see AutonomousField
 * @see ContainerState
 */
public interface AutonomousState {

    /**
     * Updates the internal state. This method is invoked <i>after</i>
     * the state has been updated by the adapter.
     */
    void update();

}
