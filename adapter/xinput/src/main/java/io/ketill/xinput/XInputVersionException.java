package io.ketill.xinput;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Signals that the version of X-input on this machine does not meet
 * a minimum version requirement, or is unavailable entirely.
 */
public final class XInputVersionException extends XInputException {

    private static String getMessage(XInputVersion version) {
        return "expecting " + version.getDescription() + " or higher";
    }

    private final XInputVersion currentVersion;
    private final XInputVersion minimumVersion;

    XInputVersionException(@Nullable XInputVersion currentVersion,
                           @NotNull XInputVersion minimumVersion) {
        super(getMessage(minimumVersion));
        this.currentVersion = currentVersion;
        this.minimumVersion = minimumVersion;
    }

    /**
     * Returns the current version of X-input.
     *
     * @return the current version of X-input, {@code null} if
     * it is not available on this machine.
     */
    public @Nullable XInputVersion getCurrentVersion() {
        return this.currentVersion;
    }

    /**
     * Returns the required minimum version of X-input.
     *
     * @return the required minimum version of X-input.
     */
    public @NotNull XInputVersion getMinimumVersion() {
        return this.minimumVersion;
    }

}
