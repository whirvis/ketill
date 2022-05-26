package io.ketill.glfw;

/**
 * An interface used by {@link DeviceGuids} to determine what operating
 * system it is currently running on.
 *
 * @see DeviceGuids#addSystem(String, OsDeterminant)
 */
@FunctionalInterface
public interface OsDeterminant {

    /**
     * @return {@code true} if this determinant believes it is the current
     * operating system which it represents, {@code false} otherwise.
     */
    boolean isCurrentOs();

}
