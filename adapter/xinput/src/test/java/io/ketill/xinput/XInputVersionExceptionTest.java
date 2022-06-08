package io.ketill.xinput;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XInputVersionExceptionTest {

    private XInputVersion current;
    private XInputVersion minimum;
    private XInputVersionException exception;

    @BeforeEach
    void createException() {
        this.current = XInputVersion.V1_3;
        this.minimum = XInputVersion.V1_4;
        this.exception = new XInputVersionException(current, minimum);
    }

    @Test
    void testGetCurrentVersion() {
        assertSame(current, exception.getCurrentVersion());
    }

    @Test
    void testGetExpectedVersion() {
        assertSame(minimum, exception.getMinimumVersion());
    }

}
