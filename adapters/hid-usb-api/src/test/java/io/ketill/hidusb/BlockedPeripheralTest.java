package io.ketill.hidusb;

import io.ketill.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class BlockedPeripheralTest {

    @Test
    void verifyEquals() {
        EqualsVerifier.forClass(BlockedPeripheral.class)
                .withNonnullFields("peripheral").verify();
    }

    @Test
    void verifyToString() {
        BlockedPeripheral<Object> blocked =
                new BlockedPeripheral<>(new Object(), null, false);
        ToStringVerifier.forClass(BlockedPeripheral.class, blocked).verify();
    }

}
