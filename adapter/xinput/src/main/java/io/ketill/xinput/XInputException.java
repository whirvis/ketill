package io.ketill.xinput;

import io.ketill.KetillException;
import org.jetbrains.annotations.Nullable;

/**
 * Signals that an error relating to the XInput module for Ketill I/O
 * has occurred.
 */
public class XInputException extends KetillException {

    XInputException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    XInputException(@Nullable String message) {
        super(message);
    }

}
