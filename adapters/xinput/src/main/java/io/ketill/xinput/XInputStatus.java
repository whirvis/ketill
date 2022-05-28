package io.ketill.xinput;

import com.github.strikerx3.jxinput.natives.XInputNatives;

/**
 * Used to check the availability of X-input.
 *
 * @see #isAvailable()
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

    static void requireAvailable() {
        if (!isAvailable()) {
            throw new XInputException("X-input is not available");
        }
    }

}
