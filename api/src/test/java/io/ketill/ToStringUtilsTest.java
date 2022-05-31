package io.ketill;

import org.junit.jupiter.api.Test;

import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class ToStringUtilsTest {

    @Test
    void testGetJoiner() {
        /*
         * It would not make sense to generate a result for toString() with
         * a null object or a null super result. As such, assume these were
         * mistakes by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> ToStringUtils.getJoiner(null));
        assertThrows(NullPointerException.class,
                () -> ToStringUtils.getJoiner(null, new Object()));
        assertThrows(NullPointerException.class,
                () -> ToStringUtils.getJoiner("", null));

        assertNotNull(ToStringUtils.getJoiner(new Object()));

        /* generate toString() result for next test */
        IoDevice device = mock(IoDevice.class);
        StringJoiner joiner = ToStringUtils.getJoiner(device);
        String str = joiner.add("field=value").toString();

        /*
         * The returned StringJoiner should have a prefix which starts
         * with the simple name of the object's class, followed by a left
         * bracket. Afterwards, it should contain the data we placed into
         * it. Finally, it should close off with a right bracket.
         */
        String prefix = device.getClass().getSimpleName() + "[";
        assertTrue(str.startsWith(prefix));
        assertTrue(str.contains("field=value"));
        assertTrue(str.endsWith("]"));

        /*
         * Since the child created here has no extra data added to it,
         * the result should be the same as the original.
         */
        StringJoiner child = ToStringUtils.getJoiner(str, device);
        assertEquals(str, child.toString());
    }

}
