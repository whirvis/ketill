package io.ketill.awt;

import io.ketill.MappingType;
import io.ketill.pc.KeyboardKey;

import java.awt.event.KeyEvent;

/**
 * A mapping for a {@link KeyboardKey} used by {@link AwtKeyboardAdapter}.
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

}
