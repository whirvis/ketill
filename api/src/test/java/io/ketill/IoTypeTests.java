package io.ketill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class IoTypeTests {

    @Test
    void aliasesMatch() {
        assertEquals(IoType.INPUT, IoType.IN);
        assertEquals(IoType.OUTPUT, IoType.OUT);
    }

}
