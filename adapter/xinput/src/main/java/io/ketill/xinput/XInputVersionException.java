package io.ketill.xinput;

import org.jetbrains.annotations.NotNull;

/**
 * Signals that the version of XInput on this machine does not meet
 * a minimum version requirement, or is unavailable entirely.
 */
public final class XInputVersionException extends XInputException {

    private static String getMessage(XInputVersion version) {
        return "expecting " + version.getDescription() + " or higher";
    }

    /**
     * The current version of XInput.
     */
    private final XInputVersion currentVersion;

    /**
     * The required minimum version of XInput.
     */
    private final XInputVersion minimumVersion;

    XInputVersionException(@NotNull XInputVersion currentVersion,
                           @NotNull XInputVersion minimumVersion) {
        super(getMessage(minimumVersion));
        this.currentVersion = currentVersion;
        this.minimumVersion = minimumVersion;
    }

    /**
     * Returns the current version of XInput.
     *
     * @return the current version of XInput.
     */
    public @NotNull XInputVersion getCurrentVersion() {
        return this.currentVersion;
    }

    /**
     * Returns the required minimum version of XInput.
     *
     * @return the required minimum version of XInput.
     */
    public @NotNull XInputVersion getMinimumVersion() {
        return this.minimumVersion;
    }

}
