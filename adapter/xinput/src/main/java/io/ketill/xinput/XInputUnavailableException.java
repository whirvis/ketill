package io.ketill.xinput;

/**
 * Signals that X-input is not available on this machine when
 * it is required to be.
 */
public final class XInputUnavailableException extends XInputException {

    XInputUnavailableException() {
        super("X-input not available");
    }

}
