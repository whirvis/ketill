package io.ketill.xinput;

import org.jetbrains.annotations.Nullable;

/**
 * Signals that XInput encountered an error during setup.
 */
public final class XInputSetupException extends XInputException {

    XInputSetupException(@Nullable Throwable cause) {
        super("Failure to load XInput", cause);
    }

}
