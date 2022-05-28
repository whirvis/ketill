package io.ketill.hidusb.gc;

import io.ketill.gc.GcController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.usb4java.LibUsbException;
import org.usb4java.Transfer;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class GcWiiUAdapterTest {

    private LibUsbDeviceGc usbDeviceGc;
    private GcWiiUAdapter adapter;
    private GcController controller;

    private TransferSpy transferSpy;

    @BeforeEach
    void createAdapter() {
        this.usbDeviceGc = mock(LibUsbDeviceGc.class);
        this.adapter = new GcWiiUAdapter(usbDeviceGc);
        this.controller = new GcController(adapter.getSlotSupplier(0));
        this.transferSpy = MockLibUsb.interceptTransfers(usbDeviceGc);
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class,
                () -> new GcWiiUAdapter(null));
    }

    @Test
    void testIsSlotConnected() {
        /*
         * It would not make sense to check if a controller connected to
         * a port out of bounds is connected to the adapter. Assume this
         * was a mistake by the user and throw an exception.
         */
        assertThrows(IndexOutOfBoundsException.class,
                () -> adapter.isSlotConnected(-1));
        assertThrows(IndexOutOfBoundsException.class,
                () -> adapter.isSlotConnected(GcWiiUAdapter.SLOT_COUNT));

        /*
         *
         */
        for (int i = 0; i < GcWiiUAdapter.SLOT_COUNT; i++) {

            assertFalse(adapter.isSlotConnected(i));

            int offset = 1 + (i * 9);
            int length = GcWiiUAdapter.DATA_LEN;
            ByteBuffer buffer = ByteBuffer.allocate(length);
            buffer.put(GcWiiUAdapter.DATA_ID);
            for(int j = 0; j < GcWiiUAdapter.DATA_LEN - 1; j++) {
                buffer.put((byte) (j == offset ? 0x10 : 0x00));
            }
            buffer.flip();
            adapter.processTransfer(buffer);

            adapter.poll();

            assertTrue(adapter.isSlotConnected(i));
        }

        /* close adapter for next test */
        adapter.close();

        /*
         * Once an adapter has been closed, it is no longer possible to
         * reliably verify, if at all, that a controller is connected to
         * any of the available ports.
         */
        assertThrows(IllegalStateException.class,
                () -> adapter.isSlotConnected(0));
    }

    @Test
    void testGetSlotSupplier() {
        assertThrows(IndexOutOfBoundsException.class,
                () -> adapter.getSlotSupplier(-1));
        assertThrows(IndexOutOfBoundsException.class,
                () -> adapter.getSlotSupplier(GcWiiUAdapter.SLOT_COUNT));

        for (int i = 0; i < GcWiiUAdapter.SLOT_COUNT; i++) {
            assertNotNull(adapter.getSlotSupplier(i));
        }

        adapter.close();
        assertThrows(IllegalStateException.class,
                () -> adapter.getSlotSupplier(0));
    }

    @Test
    void testInitAdapter() {
        /* poll twice for next test */
        adapter.poll();
        adapter.poll();

        /*
         * When a GameCube Wii U adapter is polled for the first time, it
         * should be immediately initialized. Initialization is necessary
         * for the adapter o reply with controller data when requested or
         * fulfill commands. In later polls, it should not be initialized
         * again. Doing so is unnecessary and would lower performance.
         */
        /* @formatter:off */
        verify(usbDeviceGc, times(1))
                .claimInterface(eq((byte) 0x00));
        verify(usbDeviceGc, times(1))
                .interruptTransfer(eq((byte) 0x02), any(), eq(0L));
        /* @formatter:on */
    }

    @Test
    void testSendRumblePacket() {
        ArgumentMatcher<Transfer> matchesRumbleTransfer = ((t) -> {
            InterceptedTransfer it = transferSpy.getTransfer(t);
            int packetId = it.buffer.get(0) & 0xFF;
            return packetId == GcWiiUAdapter.RUMBLE_ID;
        });

        /*
         * Since the rumble state of no controllers have been changed (no
         * controllers have started or stopped rumbling), the next adapter
         * poll should not result in a rumble packet being sent.
         */
        adapter.poll();
        verify(usbDeviceGc, never())
                .submitTransfer(argThat(matchesRumbleTransfer));

        /* begin rumbling for next test */
        controller.rumble(1.0F);
        controller.poll();

        /*
         * Once the rumble state of a controller has changed, the next
         * adapter poll should result in a rumble packet being sent.
         */
        adapter.poll();
        verify(usbDeviceGc, times(1))
                .submitTransfer(argThat(matchesRumbleTransfer));

        /* stop rumbling for next test */
        controller.rumble(0.0F);
        controller.poll();

        /*
         * In the event an exception occurs while transferring a rumble
         * packet to the device, it should be ignored. This is an accepted
         * possibility, and will likely fix itself on the next transfer.
         */
        doThrow(LibUsbException.class).when(usbDeviceGc)
                .submitTransfer(argThat(matchesRumbleTransfer));
        assertDoesNotThrow(() -> adapter.poll());
    }

    @Test
    void testPoll() {
        AtomicReference<Consumer<ByteBuffer>> endpointCallback =
                new AtomicReference<>();
        ArgumentMatcher<Transfer> matchesEndpointIn = ((t) -> {
            InterceptedTransfer it = transferSpy.getTransfer(t);
            endpointCallback.set(it.callback);
            return it.endpoint == GcWiiUAdapter.ENDPOINT_IN;
        });

        adapter.poll();

        /*
         * Since this adapter uses asynchronous I/O, it must tell LibUSB
         * that it wants to handle any pending events. Failing to so will
         * result in no input for the controller slots coming in.
         */
        verify(usbDeviceGc).handleEventsCompleted(0L);

        verify(usbDeviceGc, times(1))
                .submitTransfer(argThat(matchesEndpointIn));

        adapter.poll();

        verify(usbDeviceGc, times(1))
                .submitTransfer(argThat(matchesEndpointIn));

        ByteBuffer unknownPacket = ByteBuffer.allocate(1);
        unknownPacket.put((byte) 0x38);
        unknownPacket.flip();

        endpointCallback.get().accept(unknownPacket);

        adapter.poll();

        verify(usbDeviceGc, times(1))
                .submitTransfer(argThat(matchesEndpointIn));

        ByteBuffer dataPacket = ByteBuffer.allocate(GcWiiUAdapter.DATA_LEN);
        dataPacket.put(GcWiiUAdapter.DATA_ID);
        dataPacket.put(new byte[GcWiiUAdapter.DATA_LEN - 1]);
        dataPacket.flip();

        endpointCallback.get().accept(dataPacket);

        adapter.poll();

        verify(usbDeviceGc, times(2))
                .submitTransfer(argThat(matchesEndpointIn));
    }

    @Test
    void testClose() {

    }

}
