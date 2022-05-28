package io.ketill.hidusb.gc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usb4java.Transfer;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class InterceptedTransfer {

    public final @NotNull Transfer transfer;
    public final byte endpoint;
    public final @NotNull ByteBuffer buffer;
    public final @Nullable Consumer<ByteBuffer> callback;
    public final @Nullable Object userData;
    public final long timeout;

    public InterceptedTransfer(@NotNull Transfer transfer, byte endpoint,
                               @NotNull ByteBuffer buffer,
                               @Nullable Consumer<ByteBuffer> callback,
                               @Nullable Object userData, long timeout) {
        this.transfer = transfer;
        this.endpoint = endpoint;
        this.buffer = buffer;
        this.callback = callback;
        this.userData = userData;
        this.timeout = timeout;
    }

}
