package io.ketill.hidusb;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DeviceInfoTest {

    @Test
    void __init__() {
        /*
         * Vendor IDs are an unsigned short. Any value outside
         * the range of 0x0000 to 0xFFFF is out of bounds.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new DeviceInfo(Integer.MIN_VALUE, 0x0000));
        assertThrows(IllegalArgumentException.class,
                () -> new DeviceInfo(Integer.MAX_VALUE, 0x0000));

        /*
         * Product IDs are an unsigned short. Any value outside
         * the range of 0x0000 to 0xFFFF is out of bounds.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new DeviceInfo(0x0000, Integer.MIN_VALUE));
        assertThrows(IllegalArgumentException.class,
                () -> new DeviceInfo(0x0000, Integer.MAX_VALUE));

        /*
         * Any randomly generated number within a range 0x0000 to
         * 0xFFFF should not result in an exception being thrown
         * when constructing an instance of DeviceInfo.
         */
        Random random = new Random();
        int vendorId = random.nextInt(0xFFFF);
        int productId = random.nextInt(0xFFFF);
        assertDoesNotThrow(() -> new DeviceInfo(vendorId, productId));
    }

}
