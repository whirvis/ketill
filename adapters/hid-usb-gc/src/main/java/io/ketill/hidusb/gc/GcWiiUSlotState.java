package io.ketill.hidusb.gc;

import io.ketill.AdapterSupplier;
import io.ketill.gc.GcController;

import java.util.Arrays;

final class GcWiiUSlotState {

    private static final int DATA_LEN = 9;
    private static final int BUTTON_COUNT = 12;
    private static final int ANALOG_COUNT = 6;

    final byte[] data;

    private int type;
    private final boolean[] buttons;
    private final int[] analogs;
    private boolean rumbling;

    final AdapterSupplier<GcController> supplier;

    GcWiiUSlotState() {
        this.data = new byte[DATA_LEN];
        this.buttons = new boolean[BUTTON_COUNT];
        this.analogs = new int[ANALOG_COUNT];
        this.supplier = (c, r) -> new LibUsbGcAdapter(c, r, this);
    }

    boolean isConnected() {
        return this.type > 0;
    }

    boolean isPressed(int gcButton) {
        return this.buttons[gcButton];
    }

    int getAxis(int gcAxis) {
        return this.analogs[gcAxis];
    }

    boolean isRumbling() {
        return this.rumbling;
    }

    void setRumbling(boolean rumbling) {
        this.rumbling = rumbling;
    }

    void poll() {
        int offset = 0;

        /*
         * The first byte is the current controller type. This is
         * used to determine if the controller is connected, and
         * if it self-reports to be wireless. Wireless controllers
         * are usually Wavebird controllers. However, there is no
         * guarantee for this.
         */
        this.type = (data[offset++] & 0xFF) >> 4;

        /*
         * The next two bytes of the data payload store the button
         * states. Each button state is stored using a single bit
         * for the next two bytes. As such, bit shifting is used
         * to determine if a button is pressed.
         */
        short buttonStates = 0;
        buttonStates |= (data[offset++] & 0xFF);
        buttonStates |= (data[offset++] & 0xFF) << 8;
        for (int i = 0; i < buttons.length; i++) {
            this.buttons[i] = (buttonStates & (1 << i)) != 0;
        }

        /*
         * The value of each analog axis is stored using a single
         * byte. The amount of bytes that are read is determined
         * by the amount of axes there are on the controller.
         */
        for (int i = 0; i < analogs.length; i++) {
            this.analogs[i] = data[offset++] & 0xFF;
        }
    }

    void reset() {
        Arrays.fill(data, (byte) 0x00);
        this.poll(); /* use zeroed out data */
        this.setRumbling(false);
    }

}
