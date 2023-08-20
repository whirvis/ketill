package io.ketill;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

public final class IoStateExceptionTest {

    private static IoState<?> culprit;
    private static String message;
    private static Throwable cause;

    @BeforeAll
    static void setup() {
        culprit = mock(IoState.class);
        message = "message";
        cause = new Throwable();
    }

    @Test
    void testInitWithCulprit() {
        IoStateException a = new IoStateException(culprit, message, cause);
        assertEquals(culprit, a.getCulprit());
        assertEquals(message, a.getMessage());
        assertEquals(cause, a.getCause());

        IoStateException b = new IoStateException(culprit, message);
        assertEquals(culprit, b.getCulprit());
        assertEquals(message, b.getMessage());
        assertNull(b.getCause());

        IoStateException c = new IoStateException(culprit, cause);
        assertEquals(culprit, c.getCulprit());
        assertEquals(cause.getClass().getName(), c.getMessage());
        assertEquals(cause, c.getCause());

        IoStateException d = new IoStateException(culprit);
        assertEquals(culprit, d.getCulprit());
        assertNull(d.getMessage());
        assertNull(d.getCause());
    }

    @Test
    void testInitWithoutCulprit() {
        IoStateException e = new IoStateException(message, cause);
        assertNull(e.getCulprit());
        assertEquals(message, e.getMessage());
        assertEquals(cause, e.getCause());

        IoStateException f = new IoStateException(message);
        assertNull(f.getCulprit());
        assertEquals(message, f.getMessage());
        assertNull(f.getCause());

        IoStateException g = new IoStateException(cause);
        assertNull(g.getCulprit());
        assertEquals(cause.getClass().getName(), g.getMessage());
        assertEquals(cause, g.getCause());

        IoStateException h = new IoStateException();
        assertNull(h.getCulprit());
        assertNull(h.getMessage());
        assertNull(h.getCause());
    }

}
