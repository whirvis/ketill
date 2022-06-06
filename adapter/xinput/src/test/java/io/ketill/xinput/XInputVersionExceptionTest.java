package io.ketill.xinput;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XInputVersionExceptionTest {

    private XInputVersion minimum;
    private XInputVersionException exception;

    @BeforeEach
    void createException() {
        this.minimum = XInputVersion.V1_4;
        this.exception = new XInputVersionException(null, minimum);
    }

    @Test
    void testGetCurrentVersion() {
        assertNull(exception.getCurrentVersion());
    }

    @Test
    void testGetExpectedVersion() {
        assertSame(minimum, exception.getMinimumVersion());
    }

}
