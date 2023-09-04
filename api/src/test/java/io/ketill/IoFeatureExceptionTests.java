package io.ketill;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

final class IoFeatureExceptionTests {

    private static IoFeature<?> culprit;
    private static String message;
    private static Throwable cause;

    @BeforeAll
    static void launch() {
        culprit = mock(IoFeature.class);
        message = "message";
        cause = new Throwable();
    }

    @Test
    void initWithCulpritBehavesAsExpected() {
        IoFeatureException a = new IoFeatureException(culprit, message, cause);
        assertEquals(culprit, a.getCulprit());
        assertEquals(message, a.getMessage());
        assertEquals(cause, a.getCause());

        IoFeatureException b = new IoFeatureException(culprit, message);
        assertEquals(culprit, b.getCulprit());
        assertEquals(message, b.getMessage());
        assertNull(b.getCause());

        IoFeatureException c = new IoFeatureException(culprit, cause);
        assertEquals(culprit, c.getCulprit());
        assertEquals(cause.getClass().getName(), c.getMessage());
        assertEquals(cause, c.getCause());

        IoFeatureException d = new IoFeatureException(culprit);
        assertEquals(culprit, d.getCulprit());
        assertNull(d.getMessage());
        assertNull(d.getCause());
    }

    @Test
    void initWithoutCulpritBehavesAsExpected() {
        IoFeatureException e = new IoFeatureException(message, cause);
        assertNull(e.getCulprit());
        assertEquals(message, e.getMessage());
        assertEquals(cause, e.getCause());

        IoFeatureException f = new IoFeatureException(message);
        assertNull(f.getCulprit());
        assertEquals(message, f.getMessage());
        assertNull(f.getCause());

        IoFeatureException g = new IoFeatureException(cause);
        assertNull(g.getCulprit());
        assertEquals(cause.getClass().getName(), g.getMessage());
        assertEquals(cause, g.getCause());

        IoFeatureException h = new IoFeatureException();
        assertNull(h.getCulprit());
        assertNull(h.getMessage());
        assertNull(h.getCause());
    }

}
