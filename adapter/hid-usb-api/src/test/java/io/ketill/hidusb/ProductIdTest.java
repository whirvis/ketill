package io.ketill.hidusb;

import io.ketill.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ProductIdTest {

    @Test
    void testInit() {
        /*
         * Vendor IDs are an unsigned short. Any value outside the range
         * of 0x0000 to 0xFFFF is out of bounds.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new ProductId(Integer.MIN_VALUE, 0x0000));
        assertThrows(IllegalArgumentException.class,
                () -> new ProductId(Integer.MAX_VALUE, 0x0000));

        /*
         * Product IDs are an unsigned short. Any value outside the range
         * of 0x0000 to 0xFFFF is out of bounds.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new ProductId(0x0000, Integer.MIN_VALUE));
        assertThrows(IllegalArgumentException.class,
                () -> new ProductId(0x0000, Integer.MAX_VALUE));

        /*
         * Any randomly generated number within a range 0x0000 to 0xFFFF
         * should not result in an exception when constructing instances
         * of DeviceInfo.
         */
        Random random = new Random();
        int vendorId = random.nextInt(0xFFFF);
        int productId = random.nextInt(0xFFFF);
        assertDoesNotThrow(() -> new ProductId(vendorId, productId));
    }

    @Test
    void verifyEquals() {
        EqualsVerifier.forClass(ProductId.class).verify();
    }

    @Test
    void verifyToString() {
        ProductId id = new ProductId(0x0000, 0x0000);
        ToStringVerifier.forClass(ProductId.class, id).verify();
    }

}
