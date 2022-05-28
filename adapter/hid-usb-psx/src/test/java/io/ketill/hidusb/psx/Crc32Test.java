package io.ketill.hidusb.psx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.jupiter.api.Assertions.*;

class Crc32Test {

    private Crc32 hidCrc32;
    private CRC32 jreCrc32;

    @BeforeEach
    void setup() {
        this.hidCrc32 = new Crc32();
        this.jreCrc32 = new CRC32();
    }

    @Test
    void update() {
        /*
         * Use a randomly generated value to ensure the checksum is
         * being calculated correctly. Using a preset value could
         * possibly result in a false positive.
         */
        Random random = new Random();
        byte[] data = new byte[1024];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) random.nextInt(0x100);
        }

        /*
         * This first checksum must be calculated in order to test
         * the validity of the other checksums. Assertions are run
         * later on to ensure the base update(int) methods yields
         * an accurate CRC32 checksum.
         */
        for (byte datum : data) {
            hidCrc32.update(datum);
        }
        long hidChecksum_0 = hidCrc32.getValue();
        hidCrc32.reset();

        /*
         * The update(byte[], int, int) method is a shorthand for
         * the method update(int), with each element in the array
         * starting from the offset to the length being used to
         * update the checksum.
         */
        hidCrc32.update(data, 0, data.length);
        long hidChecksum_1 = hidCrc32.getValue();
        hidCrc32.reset();

        /*
         * The update(byte[]) method is a shorthand for the method
         * update(byte[], int, int), with the arguments for offset
         * being zero and length being the length of the array.
         */
        hidCrc32.update(data);
        long hidChecksum_2 = hidCrc32.getValue();
        hidCrc32.reset();

        /*
         * Java's built-in implementation of the CRC32 algorithm is
         * not used in this module as it's missing some methods not
         * introduced until JDK 9 (this module uses JDK 8.) However,
         * the proprietary implementation should yield checksums the
         * same as the JDK.
         */
        jreCrc32.update(data, 0, data.length);
        long jreChecksum_0 = jreCrc32.getValue();
        jreCrc32.reset();

        assertEquals(hidChecksum_0, hidChecksum_1);
        assertEquals(hidChecksum_1, hidChecksum_2);
        assertEquals(hidChecksum_2, jreChecksum_0);
    }

    @Test
    void reset() {
        hidCrc32.update(new Random().nextInt(0x100));
        hidCrc32.reset(); /* reset after using random value */
        assertEquals(0x00000000L, hidCrc32.getValue());
    }

}
