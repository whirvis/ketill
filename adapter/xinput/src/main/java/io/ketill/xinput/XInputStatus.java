package io.ketill.xinput;

import com.github.strikerx3.jxinput.natives.XInputNatives;

/**
 * Used to check the availability of X-input.
 */
public final class XInputStatus {

    private XInputStatus() {
        /* prevent instantiation */
    }

    /**
     * Since X-input may not be available on the current system, it is
     * recommended to check before attempting to use the other classes
     * in this module (such as {@link XInputXboxSeeker}.)
     *
     * @return {@code true} if X-input is available on this system,
     * {@code false} otherwise.
     */
    public static boolean isAvailable() {
        return XInputNatives.isLoaded();
    }

    /**
     * Requires that X-input be available on this system before
     * continuing execution.
     *
     * @throws XInputException if X-input is not available.
     */
    public static void requireAvailable() {
        if (!isAvailable()) {
            throw new XInputException("X-input is not available");
        }
    }

}
