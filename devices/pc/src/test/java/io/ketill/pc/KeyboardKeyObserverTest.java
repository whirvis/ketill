package io.ketill.pc;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.pc.EventAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KeyboardKeyObserverTest {

    private IoDeviceObserver deviceObserver;
    private KeyPressZ internalState;
    private KeyboardKeyObserver keyObserver;

    @BeforeEach
    void createObserver() {
        Keyboard keyboard = mock(Keyboard.class);
        this.deviceObserver = mock(IoDeviceObserver.class);
        when(deviceObserver.getDevice()).thenReturn(keyboard);

        KeyboardKey key = new KeyboardKey("key");
        this.internalState = key.getInternalState(deviceObserver);

        this.keyObserver = new KeyboardKeyObserver(key, internalState,
                deviceObserver);
    }

    @SuppressWarnings({"ConstantConditions", "UnusedAssignment"})
    @Test
    void testIsPressedImpl() {
        internalState.pressed = true;
        assertTrue(keyObserver.isPressedImpl());
        internalState.pressed = false;
        assertFalse(keyObserver.isPressedImpl());
    }

    @Test
    void testOnPress() {
        keyObserver.onPress(); /* trigger event emission */
        assertEmitted(deviceObserver, KeyboardKeyPressEvent.class);
    }

    @Test
    void testOnHold() {
        keyObserver.onHold(); /* trigger event emission */
        assertEmitted(deviceObserver, KeyboardKeyHoldEvent.class);
    }

    @Test
    void testOnRelease() {
        keyObserver.onRelease(); /* trigger event emission */
        assertEmitted(deviceObserver, KeyboardKeyReleaseEvent.class);
    }

    @Test
    void testPoll() throws InterruptedException {
        /* press keyboard key for next test */
        internalState.pressed = true;
        keyObserver.poll(); /* observer press */

        /*
         * After a keyboard key has been pressed for a long enough time
         * to be considered held down, the key state should be updated
         * by the observer to indicate that it is held down.
         */
        Thread.sleep(keyObserver.getConfig().getHoldTime());
        keyObserver.poll(); /* trigger state update */
        assertTrue(internalState.held);

        /*
         * Once a keyboard key has been released (after being pressed down),
         * the key state should be updated by the observer to indicate that
         * it is also no longer held down.
         */
        internalState.pressed = false;
        keyObserver.poll(); /* trigger state update */
        assertFalse(internalState.held);
    }

}
