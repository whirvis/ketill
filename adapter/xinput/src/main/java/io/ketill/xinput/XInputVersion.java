package io.ketill.xinput;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a version of XInput.
 *
 * @see #getDescription()
 * @see #isAtLeast(XInputVersion)
 */
public enum XInputVersion {

    /**
     * XInput 9 v1.0
     * <p>
     * <b>Availability:</b> Windows Vista only.
     */
    V1_0("XInput 9 v1.0", 0),

    /**
     * XInput v1.3
     * <p>
     * <b>Availability:</b> Windows Vista and later.
     */
    V1_3("XInput v1.3", 1),

    /**
     * XInput v1.4
     * <p>
     * <b>Availability:</b> Windows 8 and later.
     */
    V1_4("XInput v1.4", 2);

    private final String description;
    private final int level;

    XInputVersion(@NotNull String description, int level) {
        this.description = description;
        this.level = level;
    }

    /**
     * Returns the description of this version.
     *
     * @return the description of this version.
     */
    public @NotNull String getDescription() {
        return this.description;
    }

    /**
     * Returns if this version is at least a given version.
     * <p>
     * A version is considered to be "at least" another version when it
     * is greater than or equal to the version it is being compared with.
     * For example, {@link #V1_4} is greater than {@link #V1_0}. As such,
     * it is considered to be "at least" that version.
     *
     * @param version the version to compare with.
     * @return {@code true} if this version is at least {@code version},
     * {@code false} otherwise.
     * @throws NullPointerException if {@code version} is {@code null}.
     */
    public boolean isAtLeast(@NotNull XInputVersion version) {
        Objects.requireNonNull(version, "version cannot be null");
        return this.level >= version.level;
    }

}
