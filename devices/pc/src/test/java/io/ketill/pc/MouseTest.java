package io.ketill.pc;

import io.ketill.IoFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.pc.Mouse.*;
import static org.junit.jupiter.api.Assertions.*;

class MouseTest {

    private Mouse mouse;

    private void assertStateIsFeature(Object state, IoFeature<?> feature) {
        assertEquals(state, mouse.getState(feature));
    }

    @BeforeEach
    void setup() {
        this.mouse = new Mouse(MockPcAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertStateIsFeature(mouse.m1, BUTTON_M1);
        assertStateIsFeature(mouse.m2, BUTTON_M2);
        assertStateIsFeature(mouse.m3, BUTTON_M3);
        assertStateIsFeature(mouse.m4, BUTTON_M4);
        assertStateIsFeature(mouse.m5, BUTTON_M5);
        assertStateIsFeature(mouse.m6, BUTTON_M6);
        assertStateIsFeature(mouse.m7, BUTTON_M7);
        assertStateIsFeature(mouse.m8, BUTTON_M8);

        assertEquals(mouse.left, mouse.m1);
        assertEquals(mouse.right, mouse.m2);
        assertEquals(mouse.middle, mouse.m3);

        assertStateIsFeature(mouse.cursor, FEATURE_CURSOR);
    }

}