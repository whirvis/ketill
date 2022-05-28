package io.ketill.hidusb.gc;

import org.usb4java.Transfer;

import java.util.HashMap;
import java.util.Map;

public class TransferSpy {

    Map<Transfer, InterceptedTransfer> transfers;

    public TransferSpy() {
        this.transfers = new HashMap<>();
    }

    public InterceptedTransfer getTransfer(Transfer transfer) {
        return transfers.get(transfer);
    }

}
