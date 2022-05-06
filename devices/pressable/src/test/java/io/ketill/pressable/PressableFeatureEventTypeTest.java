package io.ketill.pressable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PressableFeatureEventTypeTest {

    @Test
    void testFromId() {
        /*
         * It would not make sense to get a feature event type from an
         * ID for a type that does not exist. As such, assume this was
         * a mistake by the user and throw an exception.
         */
        assertThrows(IllegalArgumentException.class,
                () -> PressableFeatureEventType.fromId(-1));

        /*
         * It would not make sense for fromId() to return a different
         * PressableFeatureEventType than the one requested. This is
         * more or less a sanity check.
         */
        for (PressableFeatureEventType value :
                PressableFeatureEventType.values()) {
            assertSame(value, PressableFeatureEventType.fromId(value.id));
        }
    }

}
