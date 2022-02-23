package io.ketill.hidusb;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DeviceInfoTest {

    @Test
    void __init__() {
        /*
         * TODO: explain
         */
        assertThrows(IllegalArgumentException.class,
                () -> new DeviceInfo(Integer.MIN_VALUE, 0x0000));
        assertThrows(IllegalArgumentException.class,
                () -> new DeviceInfo(0x0000, Integer.MIN_VALUE));

        /*
         * TODO: explain
         */
        assertThrows(IllegalArgumentException.class,
                () -> new DeviceInfo(Integer.MAX_VALUE, 0x0000));
        assertThrows(IllegalArgumentException.class,
                () -> new DeviceInfo(0x0000, Integer.MAX_VALUE));

        /*
         * TODO: explain
         */
        Random random = new Random();
        int vendorId = random.nextInt(0xFFFF);
        int productId = random.nextInt(0xFFFF);
        assertDoesNotThrow(() -> new DeviceInfo(vendorId, productId));
    }

}
