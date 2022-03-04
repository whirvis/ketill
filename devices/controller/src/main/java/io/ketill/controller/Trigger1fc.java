package io.ketill.controller;

/**
 * Interface to a read-only view of an analog trigger's state.
 */
public interface Trigger1fc {

    /**
     * @return the force being applied to the trigger.
     */
    float force();

}
