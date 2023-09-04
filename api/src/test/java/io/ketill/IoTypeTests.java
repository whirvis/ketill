package io.ketill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

final class IoTypeTests {

    @Test
    void aliasesMatch() {
        assertSame(IoType.INPUT, IoType.IN);
        assertSame(IoType.OUTPUT, IoType.OUT);
    }

}
