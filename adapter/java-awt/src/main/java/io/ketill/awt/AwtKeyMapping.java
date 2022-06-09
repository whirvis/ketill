package io.ketill.awt;

import io.ketill.MappingType;
import io.ketill.ToStringUtils;
import io.ketill.pc.KeyboardKey;

import java.awt.event.KeyEvent;
import java.util.Objects;

/**
 * A mapping for a {@link KeyboardKey} used by {@link AwtKeyboardAdapter}.
 * <p>
 * <b>Thread safety:</b> This class is <i>thread-safe.</i>
 *
 * @see AwtKeyboardAdapter#mapKey(KeyboardKey, int, int)
 */
@MappingType
public final class AwtKeyMapping {

    /**
     * The AWT keycode.
     *
     * @see KeyEvent
     */
    public final int keyCode;

    /**
     * The location on the keyboard.
     *
     * @see KeyEvent
     */
    public final int keyLocation;

    /**
     * Constructs a new {@code KeyMapping}.
     *
     * @param keyCode     the AWT keycode.
     * @param keyLocation the location on the keyboard.
     * @see KeyEvent
     */
    public AwtKeyMapping(int keyCode, int keyLocation) {
        this.keyCode = keyCode;
        this.keyLocation = keyLocation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyCode, keyLocation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AwtKeyMapping that = (AwtKeyMapping) o;
        return keyCode == that.keyCode && keyLocation == that.keyLocation;
    }

    /* @formatter:off */
    @Override
    public String toString() {
        return ToStringUtils.getJoiner(this)
                .add("keyCode=" + keyCode)
                .add("keyLocation=" + keyLocation)
                .toString();
    }
    /* @formatter:on */

}
