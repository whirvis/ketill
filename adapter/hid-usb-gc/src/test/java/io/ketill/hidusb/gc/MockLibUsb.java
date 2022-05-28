package io.ketill.hidusb.gc;

import org.jetbrains.annotations.NotNull;
import org.usb4java.Transfer;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MockLibUsb {

    public static @NotNull TransferSpy interceptTransfers(@NotNull LibUsbDeviceGc device) {
        TransferSpy spy = new TransferSpy();
        doAnswer(a -> {
            Transfer transfer = a.getArgument(0);
            Byte endpoint = a.getArgument(1);
            ByteBuffer buffer = a.getArgument(2);
            Consumer<ByteBuffer> callback = a.getArgument(3);
            Object userData = a.getArgument(4);
            Long timeout = a.getArgument(5);
            InterceptedTransfer intercepted =
                    new InterceptedTransfer(transfer, endpoint, buffer,
                            callback, userData, timeout);
            spy.transfers.put(transfer, intercepted);
            return null;
        }).when(device).fillInterruptTransfer(any(), anyByte(), any(), any(),
                any(), anyLong());
        return spy;
    }

}
