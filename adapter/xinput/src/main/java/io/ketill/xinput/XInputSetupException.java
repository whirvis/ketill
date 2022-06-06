package io.ketill.xinput;

import org.jetbrains.annotations.Nullable;

/**
 * Signals that X-input encountered an error during setup.
 */
public final class XInputSetupException extends XInputException {

    XInputSetupException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

}
