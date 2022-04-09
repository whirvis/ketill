package io.ketill.hidusb;

import io.ketill.KetillException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class PeripheralSeekerTest {

    @Test
    void __init__() {
        /*
         * For stability, creating a peripheral seeker with
         * a scan interval less than 1000ms is not allowed.
         * Testing done by hand has shown intervals lower
         * than 1000ms introduce bugs that are simply not
         * worth the time fixing.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new MockPeripheralSeeker(0L));
    }

    private MockPeripheralSeeker seeker;

    @BeforeEach
    void setup() {
        PeripheralSeeker.scanWaitDisabled = true;
        this.seeker = new MockPeripheralSeeker();
    }

    @Test
    void minimumScanInterval() {
        /*
         * Since the mock PeripheralSeeker uses the super
         * constructor, it should be using the default scan
         * interval, which is the minimum value allowed.
         */
        assertEquals(PeripheralSeeker.MINIMUM_SCAN_INTERVAL,
                seeker.scanIntervalMs);
    }

    @Test
    void isTargetingProduct() {
        /*
         * It would not make sense to see if a null product
         * ID is currently being targeted. As such, assume
         * this was a user mistake and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.isTargetingProduct(null));

        int vendorId = 0x1234, productId = 0x5678;

        seeker.targetProduct(vendorId, productId);
        assertTrue(seeker.isTargetingProduct(vendorId, productId));
        seeker.dropProduct(vendorId, productId);
        assertFalse(seeker.isTargetingProduct(vendorId, productId));
    }

    @Test
    void isTargetingPeripheral() {
        /*
         * It makes no sense to check if a null peripheral
         * is currently being targeted. Assume this was a
         * mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.isTargetingPeripheral(null));

        int vendorId = 0x1234, productId = 0x5678;
        MockPeripheral peripheral = new MockPeripheral();

        /*
         * If a PeripheralSeeker implementations returns a
         * null value for the ID of a peripheral, it has not
         * been implemented correctly. Let the user know the
         * implementation they are using has a bug.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.isTargetingPeripheral(peripheral));

        peripheral.id = new ProductId(vendorId, productId);
        assertFalse(seeker.isTargetingPeripheral(peripheral));

        seeker.targetProduct(vendorId, productId);
        assertTrue(seeker.isTargetingPeripheral(peripheral));
    }

    @Test
    void targetProduct() {
        /*
         * It would not make sense to target a peripheral
         * with a null product ID. Assume this was a mistake
         * by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.targetProduct(null));

        AtomicBoolean targeted = new AtomicBoolean();
        seeker.onTargetProduct((s, p) -> targeted.set(true));

        seeker.targetProduct(0x0000, 0x0000);
        assertTrue(targeted.get());
        assertTrue(seeker.targetedProduct);
    }

    @Test
    void dropProduct() {
        /*
         * It would not make sense to drop a product with a
         * null product ID. As such, assume this was a user
         * mistake and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.dropProduct(null));

        MockPeripheral peripheral = new MockPeripheral();
        peripheral.id = new ProductId(0x1234, 0x5678);

        /*
         * When dropping a product that has not yet been
         * targeted, this method should do nothing.
         */
        seeker.dropProduct(peripheral.id);
        assertFalse(seeker.disconnectedPeripheral);
        assertFalse(seeker.shutdownPeripheral);

        /* attach peripheral for next test */
        seeker.targetProduct(peripheral.id);
        seeker.attachMock(peripheral);

        AtomicBoolean dropped = new AtomicBoolean();
        seeker.onDropProduct((s, p) -> dropped.set(true));

        /*
         * It would not make sense for peripherals of a
         * certain product to linger after their product
         * has been dropped. As such, the peripheral
         * seeker should detach and disconnect them.
         */
        seeker.dropProduct(peripheral.id);
        assertTrue(dropped.get());
        assertTrue(seeker.droppedProduct);
        assertTrue(seeker.shutdownPeripheral);
        assertTrue(seeker.disconnectedPeripheral);
    }

    @Test
    void isPeripheralBlocked() {
        /*
         * It makes no sense to check if a null peripheral
         * is currently blocked. As such, assume this was a
         * mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.isPeripheralBlocked(null));

        MockPeripheral peripheral = new MockPeripheral(0x1234);

        seeker.blockPeripheral(peripheral, false);
        assertTrue(seeker.isPeripheralBlocked(peripheral));

        /*
         * Peripherals with the same hash code are assumed
         * to be the same physical device. This is due to
         * the fact new object instances are often created
         * for the physical device, usually after one is
         * detached and then re-attached to the system.
         */
        MockPeripheral duplicate = new MockPeripheral(0x1234);
        assertTrue(seeker.isPeripheralBlocked(duplicate));

        seeker.unblockPeripheral(peripheral);
        assertFalse(seeker.isPeripheralBlocked(peripheral));
    }

    @Test
    void blockPeripheral() {
        /*
         * It makes no sense to block a null peripheral.
         * As such, assume this was a mistake by the user
         * and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.blockPeripheral(null, true));

        /*
         * Create a peripheral to block for the following
         * tests. This will share the hash code of another
         * peripheral instance, by intention. This fact will
         * be used for a later test.
         */
        MockPeripheral peripheral = new MockPeripheral(0x1234);
        peripheral.id = new ProductId(0x1234, 0x5678);
        seeker.targetProduct(peripheral.id);

        /* connect peripheral for next test */
        seeker.attachMock(peripheral);

        /* set callback for next test */
        AtomicBoolean afterDisconnect = new AtomicBoolean();
        seeker.onBlockPeripheral((s, b) ->
                /*
                 * If seeker.disconnectedPeripherals equals
                 * true when this callback is executed, that
                 * means it was executed after the peripheral
                 * was disconnected (as it should be).
                 */
                afterDisconnect.set(seeker.disconnectedPeripheral));

        seeker.blockPeripheral(peripheral, true);

        /*
         * If two peripheral instances share a hash code,
         * that means they are the same physical device in
         * the real world. This ensures peripheral blocking
         * functions even if a new instance is created for a
         * physical device. It usually occurs when a device
         * is unplugged and then plugged back in.
         */
        int hashCopy = peripheral.hashCode;
        MockPeripheral peripheralCopy = new MockPeripheral(hashCopy);
        assertTrue(seeker.isPeripheralBlocked(peripheralCopy));

        /*
         * Once the peripheral has been blocked, it cannot be
         * blocked again unless previously unblocked. This is
         * to prevent blocking arguments from being silently
         * overridden on accident.
         *
         * Furthermore, the peripheral copy should result in
         * an exception as it is considered to be the same
         * peripheral as the original.
         */
        assertThrows(IllegalStateException.class,
                () -> seeker.blockPeripheral(peripheral, true));
        assertThrows(IllegalStateException.class,
                () -> seeker.blockPeripheral(peripheralCopy, true));

        /*
         * Since the peripheral was already connected when
         * it was blocked, it should have been immediately
         * disconnected by the peripheral seeker.
         */
        assertTrue(seeker.blockedPeripheral);
        assertTrue(seeker.shutdownPeripheral);
        assertTrue(seeker.disconnectedPeripheral);
        assertTrue(afterDisconnect.get());
    }

    @Test
    void unblockPeripheral() {
        /*
         * It makes no sense to unblock a null peripheral.
         * As such, assume this was a mistake by the user
         * and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.unblockPeripheral(null));

        MockPeripheral peripheral = new MockPeripheral();

        /* set callback for next test */
        AtomicBoolean unblocked = new AtomicBoolean();
        seeker.onUnblockPeripheral((s, b) -> unblocked.set(true));

        /*
         * It makes no sense to unblock a peripheral if
         * it was not already blocked beforehand. As such,
         * the callback should not be executed.
         */
        seeker.unblockPeripheral(peripheral);
        assertFalse(seeker.unblockedPeripheral);
        assertFalse(unblocked.get());

        /* block peripheral for next test */
        seeker.blockPeripheral(peripheral, false);

        seeker.unblockPeripheral(peripheral);
        assertTrue(seeker.unblockedPeripheral);
        assertTrue(unblocked.get());
    }

    @Test
    void attachPeripheral() {
        /* attach peripheral for next test */
        MockPeripheral peripheral = new MockPeripheral();
        peripheral.id = new ProductId(0x1234, 0x5678);

        /*
         * Although the mock peripheral has been attached,
         * it is not currently targeted by the peripheral.
         * seeker. As such, it should not be attached.
         */
        seeker.reset();
        seeker.targetProduct(0x0000, 0x0000);
        seeker.attachMock(peripheral);
        assertFalse(seeker.setupPeripheral);

        /*
         * The product of the peripheral is now targeted.
         * The peripheral seeker should now connect it on
         * the first peripheral scan that it is detected.
         */
        seeker.reset();
        seeker.targetProduct(peripheral.id);
        seeker.attachMock(peripheral);
        assertTrue(seeker.setupPeripheral);
        assertTrue(seeker.connectedPeripheral);

        /*
         * Once a peripheral has been attached, it should
         * not be re-attached on the next scan. They should
         * only be re-attached if previously detached.
         */
        seeker.reset();
        seeker.attachMock(peripheral);
        assertFalse(seeker.setupPeripheral);
        assertFalse(seeker.connectedPeripheral);

        /* block peripheral for next test */
        MockPeripheral blockedPeripheral = new MockPeripheral();
        blockedPeripheral.id = peripheral.id;
        seeker.blockPeripheral(blockedPeripheral, false);

        /*
         * When a peripheral is blocked, it should not be
         * attached by the seeker even if detected on the
         * next peripheral scan.
         */
        seeker.reset();
        seeker.attachMock(blockedPeripheral);
        assertFalse(seeker.setupPeripheral);

        /* create broken peripheral for next test */
        MockPeripheral brokenPeripheral = new MockPeripheral();
        brokenPeripheral.id = peripheral.id;

        /*
         * If an error occurs while attaching a peripheral,
         * the peripheral seeker should block the peripheral
         * until it is detached. This allows for connection
         * to be re-attempted and prevents a seeker crash.
         */
        seeker.reset();
        seeker.errorOnSetup = true;
        seeker.attachMock(brokenPeripheral);
        assertTrue(seeker.failedSetup);
        assertFalse(seeker.connectedPeripheral);
        assertTrue(seeker.blockedPeripheral);

        /* unblock peripheral for next test */
        seeker.unblockPeripheral(blockedPeripheral);

        /*
         * It is possible for peripheral to be blocked while
         * it is being connected. The peripheral seeker must
         * be aware of this. If this occurs, the seeker must
         * not connect the peripheral.
         */
        seeker.reset();
        seeker.blockOnSetup = true;
        seeker.attachMock(blockedPeripheral);
        assertTrue(seeker.setupPeripheral);
        assertFalse(seeker.connectedPeripheral);
    }

    @Test
    void detachPeripheral() {
        MockPeripheral peripheral = new MockPeripheral();
        peripheral.id = new ProductId(0x1234, 0x5678);
        seeker.targetProduct(peripheral.id);

        /* attach peripheral for next test */
        seeker.attachMock(peripheral);

        /*
         * When blocking a peripheral, the user can specify
         * for it to be unblocked after it is detached. This
         * ensures that functionality is operational.
         */
        seeker.reset();
        seeker.blockPeripheral(peripheral, true);
        seeker.detachMock(peripheral);
        assertFalse(seeker.isPeripheralBlocked(peripheral));

        /* attach peripheral for next test */
        seeker.attachMock(peripheral);

        /*
         * If a peripheral causes an exception to be thrown
         * while being detached, the extending class must be
         * notified. The peripheral, on the other hand, will
         * not be blocked. This is because communication is
         * already over, so blocking it would do nothing.
         */
        seeker.reset();
        seeker.errorOnShutdown = true;
        seeker.detachMock(peripheral);
        assertTrue(seeker.failedShutdown);
        assertTrue(seeker.disconnectedPeripheral);
        assertFalse(seeker.blockedPeripheral);

        /* attach peripheral for next test */
        seeker.attachMock(peripheral);

        /*
         * It is possible for peripheral to be blocked while
         * it is being disconnected. The peripheral seeker
         * must be able to handle this event. If this occurs,
         * the peripheral will still be disconnected.
         */
        seeker.reset();
        seeker.blockOnShutdown = true;
        seeker.detachMock(peripheral);
        assertTrue(seeker.shutdownPeripheral);
        assertTrue(seeker.disconnectedPeripheral);
    }

    @Test
    void isPeripheralConnected() {
        /*
         * It makes no sense to check if a null peripheral
         * is connected. As such, assume this was a mistake
         * by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.isPeripheralConnected(null));

        MockPeripheral peripheral = new MockPeripheral();
        peripheral.id = new ProductId(0x1234, 0x5678);
        seeker.targetProduct(peripheral.id);

        seeker.attachMock(peripheral);
        assertTrue(seeker.isPeripheralConnected(peripheral));

        seeker.detachMock(peripheral);
        assertFalse(seeker.isPeripheralConnected(peripheral));
    }

    @Test
    void seek() throws InterruptedException {
        /*
         * It makes no sense to seek for peripherals when
         * no products are being targeted. Assume this was
         * a mistake by the user and throw an exception.
         */
        assertThrows(KetillException.class, () -> seeker.seek());

        /* re-enable scan waiting for next test */
        PeripheralSeeker.scanWaitDisabled = false;
        seeker.targetProduct(0x0000, 0x0000);

        /*
         * In production mode, peripheral seekers will only
         * perform scans after enough time has passed since
         * the last scan. The code below calls seek() twice
         * in succession. As such, the seeker should only
         * perform a single peripheral scan.
         */
        seeker.seek().seek();
        assertEquals(1, seeker.peripheralScanCount);

        /* sleep for scan interval for next test */
        Thread.sleep(seeker.scanIntervalMs);

        /*
         * After sleeping a long enough time to satisfy the
         * scan interval cool down, running the same code
         * should cause the peripheral seeker to perform
         * just one more scan.
         */
        seeker.seek().seek();
        assertEquals(2, seeker.peripheralScanCount);
    }

    @Test
    void close() {
        MockPeripheral peripheral = new MockPeripheral();

        /*
         * When a peripheral seeker is closed, it is expected
         * to drop all currently targeted products. This is
         * because they are no longer being targeted.
         */
        AtomicBoolean dropped = new AtomicBoolean();
        seeker.onDropProduct((s, p) -> dropped.set(true));
        seeker.targetProduct(0x0000, 0x0000);

        /*
         * WHen a peripheral seeker is closed, it is expected
         * to unblock all currently blocked peripherals. This
         * is because the seeker no longer actively refuses
         * to attach them.
         */
        AtomicBoolean unblocked = new AtomicBoolean();
        seeker.onUnblockPeripheral((s, b) -> unblocked.set(true));
        seeker.blockPeripheral(peripheral, true);

        seeker.close();

        assertTrue(dropped.get());
        assertTrue(unblocked.get());

        /*
         * It would not make sense to target a product, drop
         * a product, block or unblock a peripheral, connect
         * a peripheral, or disconnect a peripheral after the
         * peripheral seeker has been closed. Assume this was
         * a mistake by the user and throw an exception.
         */
        assertThrows(IllegalStateException.class,
                () -> seeker.targetProduct(0x0000, 0x0000));
        assertThrows(IllegalStateException.class,
                () -> seeker.dropProduct(0x0000, 0x0000));
        assertThrows(IllegalStateException.class,
                () -> seeker.blockPeripheral(peripheral, true));
        assertThrows(IllegalStateException.class,
                () -> seeker.unblockPeripheral(peripheral));

        /*
         * It would not make sense to set any callbacks after
         * the peripheral seeker has been closed. None of them
         * will ever be executed. Assume this was a mistake by
         * the user and throw an exception.
         */
        assertThrows(IllegalStateException.class,
                () -> seeker.onBlockPeripheral(null));
        assertThrows(IllegalStateException.class,
                () -> seeker.onUnblockPeripheral(null));
    }

}
