package io.ketill.hidusb;

import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class ProductIdTest {

    @Test
    void __init__() {
        /*
         * Vendor IDs are an unsigned short. Any value outside
         * the range of 0x0000 to 0xFFFF is out of bounds.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new ProductId(Integer.MIN_VALUE, 0x0000));
        assertThrows(IllegalArgumentException.class,
                () -> new ProductId(Integer.MAX_VALUE, 0x0000));

        /*
         * Product IDs are an unsigned short. Any value outside
         * the range of 0x0000 to 0xFFFF is out of bounds.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new ProductId(0x0000, Integer.MIN_VALUE));
        assertThrows(IllegalArgumentException.class,
                () -> new ProductId(0x0000, Integer.MAX_VALUE));

        /*
         * Any randomly generated number within a range 0x0000 to
         * 0xFFFF should not result in an exception being thrown
         * when constructing an instance of DeviceInfo.
         */
        Random random = new Random();
        int vendorId = random.nextInt(0xFFFF);
        int productId = random.nextInt(0xFFFF);
        assertDoesNotThrow(() -> new ProductId(vendorId, productId));
    }

    @Test
    void testEquals() {
        ProductId id = new ProductId(0x1234, 0x5678);

        boolean equalsNull = id.equals(null);
        assertFalse(equalsNull);

        Object obj = new Object();
        boolean equalsObj = id.equals(obj);
        assertFalse(equalsObj);

        ProductId blank = new ProductId(0x0000, 0x0000);
        boolean equalsBlank = id.equals(blank);
        assertFalse(equalsBlank);

        @SuppressWarnings("EqualsWithItself")
        boolean equalsSelf = id.equals(id);
        assertTrue(equalsSelf);

        ProductId copy = new ProductId(id.vendorId, id.productId);
        boolean equalsCopy = id.equals(copy);
        assertTrue(equalsCopy);
    }

    @Test
    void testHashCode() {
        Random random = new Random();
        int vendorId = random.nextInt(0xFFFF);
        int productId = random.nextInt(0xFFFF);
        ProductId id = new ProductId(vendorId, productId);

        int hash = Objects.hash(vendorId, productId);
        assertEquals(hash, id.hashCode());
    }

}
