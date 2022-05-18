package io.ketill.hidusb.psx;

import io.ketill.FeatureAdapter;
import io.ketill.IoDeviceAdapter;
import io.ketill.MappedFeatureRegistry;
import io.ketill.MappingMethod;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.ButtonStateZ;
import io.ketill.controller.ControllerButton;
import io.ketill.controller.MotorVibration;
import io.ketill.controller.RumbleMotor;
import io.ketill.controller.StickPosZ;
import io.ketill.controller.TriggerStateZ;
import io.ketill.psx.LightbarColor;
import io.ketill.psx.Ps4Controller;
import org.hid4java.HidDevice;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4fc;

import java.util.Arrays;

abstract class HidPs4Adapter extends IoDeviceAdapter<Ps4Controller> {

    private static final long POKE_AWAIT = 4000L;

    /*
     * For the state of the D-pad, Sony has opted to use hardcoded IDs
     * (rather than bitfields) to represent the different directions it
     * can be pressed in. These IDs are:
     *      - 0b0000 / north
     *      - 0b0001 / north-east
     *      - 0b0010 / east
     *      - 0b0011 / south-east
     *      - 0b0100 / south
     *      - 0b0101 / south west
     *      - 0b0110 / west
     *      - 0b0111 / north west
     *      - 0b1000 / released
     *
     * Because Ketill opts to just represent the D-pad buttons like any
     * other button, it uses a pattern system as seen here. If the bits
     * match the pattern for a D-pad, it means that button is pressed.
     * For example, when a PS4 controller reports 0b0001 (north-east),
     * it means both UP and RIGHT are being pressed.
     */
    /* @formatter:off */
    static final int[]
            DPAD_PATTERNS_UP    = { 0b0000, 0b0001, 0b0111 },
            DPAD_PATTERNS_DOWN  = { 0b0011, 0b0100, 0b0101 },
            DPAD_PATTERNS_LEFT  = { 0b0101, 0b0110, 0b0111 },
            DPAD_PATTERNS_RIGHT = { 0b0001, 0b0010, 0b0011 };
    /* @formatter:on */

    /**
     * Populates an input report with the default state of a PS4
     * controller. This method exists due to the fact that input
     * report data seems to be the same across USB and Bluetooth
     * except for their offset.
     *
     * @param report the input report to populate.
     * @param offset the offset to begin writing at.
     * @return the new offset.
     */
    @SuppressWarnings("UnusedReturnValue")
    static int populateInputReport(byte[] report, int offset) {
        /*
         * The next four bytes are the axes for the left and right
         * analog sticks. Since the values of zero do not represent
         * a stick in its default position (in the middle), these
         * must also be set manually.
         */
        byte middlePos = (byte) 0x7F;
        report[offset++] = middlePos;
        report[offset++] = middlePos;
        report[offset++] = middlePos;
        report[offset++] = middlePos;

        /*
         * While most buttons on this controller use zero to indicate
         * released, the D-pad does not. Instead, a value of zero for
         * the D-pad bits mean that the north button is being pressed.
         * This is remedied by writing 0b1000 in the lower four bits,
         * which represents released for the D-pad.
         */
        byte padBits = (byte) 0b00001000;
        report[offset++] = padBits;

        return offset;
    }

    final HidDevice hidDevice;

    private final byte inputReportId;
    private final byte[] inputReport;
    private final byte[] inputReportSink;
    private final byte outputReportId;
    private final byte[] outputReport;

    private final Crc32 crc32;
    private final byte[] crcHeader;
    private long lastChecksum;
    private long lastPokeTime;
    private boolean connected;

    /**
     * @param controller     the controller which owns this adapter.
     * @param registry       the controller's mapped feature registry.
     * @param hidDevice      the HID device, must be open.
     * @param inputReportId  the input report ID.
     * @param outputReportId the output report ID.
     * @throws NullPointerException  if {@code controller}, {@code registry},
     *                               or {@code hidDevice} are {@code} null.
     * @throws IllegalStateException if {@code hidDevice} is not open.
     */
    HidPs4Adapter(@NotNull Ps4Controller controller,
                  @NotNull MappedFeatureRegistry registry,
                  @NotNull HidDevice hidDevice, byte inputReportId,
                  byte outputReportId) {
        super(controller, registry);

        this.hidDevice = hidDevice;
        if (!hidDevice.isOpen()) {
            throw new IllegalStateException("hidDevice must be open");
        }

        this.inputReportId = inputReportId;
        this.inputReport = this.generateInputReport();
        this.inputReportSink = new byte[inputReport.length];
        this.outputReportId = outputReportId;
        this.outputReport = this.generateOutputReport();

        this.crc32 = new Crc32();
        this.crcHeader = this.getChecksumHeader();
        this.lastChecksum = -1L;
        this.connected = true;
    }

    /**
     * The default state of PS4 controllers is not an array of zeros. As a
     * result, using an empty array will result in an erroneous state until
     * the first report is read. This method generates the expected initial
     * report to work around this.
     *
     * @return the generated input report.
     * @see #generateOutputReport()
     * @see #getChecksumHeader()
     */
    abstract byte[] generateInputReport();

    /**
     * The output report is used to tell a PS4 controller its current state.
     * It contains data such as the lightbar color, the rumble force, among
     * others. The array returned will be used for every report (rather than
     * generated every time an output report is sent.) This is done to save
     * memory. As such the returned array should contain the default state.
     *
     * @return the generated output report.
     * @see #generateInputReport()
     * @see #getChecksumHeader()
     */
    abstract byte[] generateOutputReport();

    /**
     * The checksum header is used to provide a correct calculation of the
     * CRC-32 checksum when sending output to the PS4 controller. By default,
     * this method returns an empty array (no header.) Override this method
     * only when the PS4 controller expects header bytes not present in the
     * output report to be used when calculating the CRC-32 checksum.
     *
     * @return the checksum header.
     * @see #generateInputReport()
     * @see #generateOutputReport()
     */
    byte[] getChecksumHeader() {
        return new byte[0]; /* no header */
    }

    @MappingMethod
    void mapDpad(@NotNull ControllerButton button, int byteOffset, int[] patterns) {
        registry.mapFeature(button, new DpadMapping(byteOffset, patterns),
                this::updateDpad);
    }

    @MappingMethod
    void mapButton(@NotNull ControllerButton button, int byteOffset, int bitIndex) {
        registry.mapFeature(button, new ButtonMapping(byteOffset, bitIndex),
                this::updateButton);
    }

    @MappingMethod
    void mapStick(@NotNull AnalogStick stick, int byteOffsetX,
                  int byteOffsetY, int thumbByteOffset, int thumbBitIndex) {
        registry.mapFeature(stick, new StickMapping(byteOffsetX, byteOffsetY,
                thumbByteOffset, thumbBitIndex), this::updateStick);
    }

    @MappingMethod
    void mapTrigger(@NotNull AnalogTrigger trigger, int byteOffset) {
        registry.mapFeature(trigger, byteOffset, this::updateTrigger);
    }

    @MappingMethod
    void mapMotor(@NotNull RumbleMotor motor, int byteOffset) {
        registry.mapFeature(motor, byteOffset, this::updateMotor);
    }

    @MappingMethod
    void mapLightbar(int byteOffset) {
        registry.mapFeature(Ps4Controller.FEATURE_LIGHTBAR, byteOffset,
                this::updateLightbar);
    }

    private boolean isPressed(int byteOffset, int bitIndex) {
        int bits = this.inputReport[byteOffset] & 0xFF;
        return (bits & (1 << bitIndex)) != 0;
    }

    @FeatureAdapter
    void updateDpad(@NotNull ButtonStateZ state, @NotNull DpadMapping mapping) {
        int bits = this.inputReport[mapping.byteOffset] & 0xFF;
        state.pressed = mapping.hasPattern(bits);
    }

    @FeatureAdapter
    void updateButton(@NotNull ButtonStateZ state,
                      @NotNull ButtonMapping mapping) {
        state.pressed = this.isPressed(mapping.byteOffset, mapping.bitIndex);
    }

    @FeatureAdapter
    void updateStick(@NotNull StickPosZ state, @NotNull StickMapping mapping) {
        int posX = this.inputReport[mapping.byteOffsetX] & 0xFF;
        int posY = this.inputReport[mapping.byteOffsetY] & 0xFF;

        boolean pressed = false;
        if (mapping.hasThumb) {
            pressed = this.isPressed(mapping.thumbByteOffset,
                    mapping.thumbBitIndex);
        }

        /*
         * This may look confusing, but it's just some normalization. The
         * analog sticks are first converted from a 0x00 to 0xFF scale to
         * a 0.0F to 1.0F scale. However, this is not sufficient for the
         * input API; it uses a scale of -1.0F to 1.0F for analog sticks.
         *
         * The X-axis starts from the left at (at 0.0F) and ends at the
         * very right (1.0F.). The Y-axis starts at the very at the very
         * top (0.0F) and ends at the very bottom (1.0F.)
         */
        state.pos.x = ((posX / 255.0F) * 2.0F) - 1.0F;
        state.pos.y = ((posY / 255.0F) * -2.0F) + 1.0F;
        state.pos.z = pressed ? -1.0F : 0.0F;
    }

    @FeatureAdapter
    void updateTrigger(@NotNull TriggerStateZ state, int byteOffset) {
        int pos = this.inputReport[byteOffset] & 0xFF;
        state.force = pos / 255.0F;
    }

    @FeatureAdapter
    void updateMotor(@NotNull MotorVibration vibration, int byteOffset) {
        byte forceByte = (byte) (vibration.getStrength() * 0xFF);
        this.outputReport[byteOffset] = forceByte;
    }

    @FeatureAdapter
    void updateLightbar(@NotNull LightbarColor color, int byteOffset) {
        Vector4fc vector = color.getVector();
        float alpha = vector.w() * 0xFF;
        this.outputReport[byteOffset] = (byte) (vector.x() * alpha);
        this.outputReport[byteOffset + 1] = (byte) (vector.y() * alpha);
        this.outputReport[byteOffset + 2] = (byte) (vector.z() * alpha);
    }

    @Override
    @MustBeInvokedByOverriders
    protected final void pollDevice() {
        long currentTime = System.currentTimeMillis();

        /*
         * If less than zero bytes are read, an error has occurred.
         * Usually, it's just that the device has disconnected. As
         * such, set the connected state to false and return from
         * this function early. This prevents erroneous input data
         * from being reported.
         */
        int read = hidDevice.read(inputReportSink);
        if (read < 0) {
            this.connected = false;
            return;
        }

        /*
         * A sink is read into in case an unexpected input report is
         * received. So long as the first byte of new report matches
         * the expected report ID, copy the bytes from the sink to
         * the current input report. If it doesn't match, ignore it
         * by not copying. This prevents non-input data from being
         * calculated (and possibly crashing the adapter.)
         */
        if (read > 0 && inputReportSink[0] == inputReportId) {
            System.arraycopy(inputReportSink, 0, inputReport, 0, read);
        }

        /*
         * For the PS4 controller to accept an output report, it must
         * have an accurate CRC32 appended to the end of the packet.
         * Otherwise, it will not accept the report.
         *
         * While not its original purpose, the CRC32 has another use.
         * It can be used to determine if an output packet should be
         * sent at all. If the new checksum is equal to the previous
         * checksum, there's no new data to send to the controller.
         */
        crc32.reset();
        crc32.update(crcHeader);
        crc32.update(outputReportId);
        crc32.update(outputReport);
        long checksum = crc32.getValue();

        /*
         * If enough time has elapsed since the last packet was sent,
         * send it regardless of the checksum. This ensures that the
         * controller does not assume connection was lost. Failing to
         * poke the controller will have undesirable effects, such as
         * causing rumble to stop early.
         */
        long pokeDelta = currentTime - lastPokeTime;

        if (lastChecksum != checksum || pokeDelta >= POKE_AWAIT) {
            /*
             * Since the checksum must be appended to the original
             * report, the bytes of the output report must be copied
             * to a buffer with additional storage for the checksum.
             */
            byte[] message = Arrays.copyOf(outputReport,
                    outputReport.length + 4);

            int offset = outputReport.length;
            message[offset] = (byte) (checksum);
            message[offset + 1] = (byte) (checksum >> 8);
            message[offset + 2] = (byte) (checksum >> 16);
            message[offset + 3] = (byte) (checksum >> 24);

            hidDevice.write(message, message.length, outputReportId);

            this.lastChecksum = checksum;
            this.lastPokeTime = currentTime;
        }
    }

    @Override
    protected final boolean isDeviceConnected() {
        return this.connected;
    }

}
