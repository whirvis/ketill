package io.ketill.awt;

import io.ketill.MappedFeatureRegistry;
import io.ketill.pc.Keyboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static io.ketill.KetillAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class AwtKeyboardAdapterTest {

    private Component component;
    private KeyListener listener;
    private Keyboard keyboard;

    @BeforeEach
    void captureKeyboard() {
        this.component = mock(Component.class);

        /*
         * The AWT keyboard listener will call addKeyboardListener() on
         * the component when it is initialized. The mock below captures
         * it, allowing it to be directly interacted with in later tests.
         */
        doAnswer(a -> {
            this.listener = a.getArgument(0);
            return null;
        }).when(component).addKeyListener(any());

        this.keyboard = AwtKeyboardAdapter.capture(component);
        keyboard.poll(); /* initialize listener */
    }

    @Test
    void testCapture() {
        assertThrows(NullPointerException.class,
                () -> AwtKeyboardAdapter.capture(null));
        assertNotNull(AwtKeyboardAdapter.capture(component));
    }

    @Test
    void testCaptureBackground() {
        assertThrows(NullPointerException.class,
                () -> AwtKeyboardAdapter.captureBackground(null));

        AwtPollWorker<Keyboard> keyboardWorker =
                AwtKeyboardAdapter.captureBackground(component);
        assertNotNull(keyboardWorker);

        /* prevent lingering background thread */
        keyboardWorker.close();
    }

    @Test
    void testMapKey() {
        /* create adapter from mocks for next test */
        Keyboard keyboard = mock(Keyboard.class);
        MappedFeatureRegistry registry = mock(MappedFeatureRegistry.class);
        AwtKeyboardAdapter adapter = new AwtKeyboardAdapter(keyboard,
                registry, component);

        /*
         * It would not make sense to map a null key. As such, assume this
         * was a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> adapter.mapKey(null, KeyEvent.VK_SPACE));
    }

    @Test
    void ensureIntendedFeaturesSupported() {
        assertAllFeaturesSupported(keyboard, Keyboard.KEY_WORLD_1,
                Keyboard.KEY_WORLD_2);
    }

    @Test
    void testUpdateKey() {
        KeyEvent event = mock(KeyEvent.class);
        when(event.getKeyCode()).thenReturn(KeyEvent.VK_SPACE);
        when(event.getKeyLocation()).thenReturn(KeyEvent.KEY_LOCATION_STANDARD);

        listener.keyPressed(event);
        keyboard.poll(); /* update keyboard keys */
        assertTrue(keyboard.space.isPressed());

        listener.keyReleased(event);
        keyboard.poll(); /* update keyboard keys */
        assertFalse(keyboard.space.isPressed());
    }

    @Test
    void testPollDevice() {
        /*
         * The AWT keyboard listener was initialized by createKeyboard()
         * during setup. As a result, there's nothing to verify here. Just
         * make sure that calling poll() does not cause an exception.
         */
        assertDoesNotThrow(() -> keyboard.poll());
    }

    @Test
    void testIsDeviceConnected() {
        /*
         * For simplicity, keyboards are assumed to always be connected to
         * the computer. As such, this method should always return true.
         */
        assertTrue(keyboard.isConnected());
    }

}
