package io.ketill.xinput;

/**
 * Signals that XInput is not available on this machine when
 * it is required to be.
 */
public final class XInputUnavailableException extends XInputException {

    XInputUnavailableException() {
        super("XInput not available");
    }

}
