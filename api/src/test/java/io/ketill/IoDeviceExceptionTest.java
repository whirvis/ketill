package io.ketill;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

public final class IoDeviceExceptionTest {

    private static IoDevice culprit;
    private static String message;
    private static Throwable cause;

    @BeforeAll
    static void launch() {
        culprit = mock(IoDevice.class);
        message = "message";
        cause = new Throwable();
    }

    @Test
    void testInitWithCulprit() {
        IoDeviceException a = new IoDeviceException(culprit, message, cause);
        assertEquals(culprit, a.getCulprit());
        assertEquals(message, a.getMessage());
        assertEquals(cause, a.getCause());

        IoDeviceException b = new IoDeviceException(culprit, message);
        assertEquals(culprit, b.getCulprit());
        assertEquals(message, b.getMessage());
        assertNull(b.getCause());

        IoDeviceException c = new IoDeviceException(culprit, cause);
        assertEquals(culprit, c.getCulprit());
        assertEquals(cause.getClass().getName(), c.getMessage());
        assertEquals(cause, c.getCause());

        IoDeviceException d = new IoDeviceException(culprit);
        assertEquals(culprit, d.getCulprit());
        assertNull(d.getMessage());
        assertNull(d.getCause());
    }

    @Test
    void testInitWithoutCulprit() {
        IoDeviceException e = new IoDeviceException(message, cause);
        assertNull(e.getCulprit());
        assertEquals(message, e.getMessage());
        assertEquals(cause, e.getCause());

        IoDeviceException f = new IoDeviceException(message);
        assertNull(f.getCulprit());
        assertEquals(message, f.getMessage());
        assertNull(f.getCause());

        IoDeviceException g = new IoDeviceException(cause);
        assertNull(g.getCulprit());
        assertEquals(cause.getClass().getName(), g.getMessage());
        assertEquals(cause, g.getCause());

        IoDeviceException h = new IoDeviceException();
        assertNull(h.getCulprit());
        assertNull(h.getMessage());
        assertNull(h.getCause());
    }

}
