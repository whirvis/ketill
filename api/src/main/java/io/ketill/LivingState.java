package io.ketill;

/**
 * A special interface for objects which represent the state of an
 * {@link IoFeature}. When implemented, the {@link #update()} method
 * is executed each time an I/O device is polled.
 */
public interface LivingState {

    void update();

}
