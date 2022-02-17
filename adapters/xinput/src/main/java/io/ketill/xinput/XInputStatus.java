package io.ketill.xinput;

import com.github.strikerx3.jxinput.natives.XInputNatives;

public class XInputStatus {

    private static final boolean AVAILABLE = XInputNatives.isLoaded();

    /**
     * Since X-input may not be available on the current system, it is
     * recommended to check before attempting to use the other classes
     * in this module (such as {@link XInputXboxSeeker}.)
     *
     * @return {@code true} if X-input is available on this system,
     * {@code false} otherwise.
     */
    public static boolean isAvailable() {
        return AVAILABLE;
    }

    protected static void requireAvailable() {
        if (!AVAILABLE) {
            throw new XInputException("X-input is not available");
        }
    }

}
