package com.whirvis.kibasan.adapter.dualshock;

import com.whirvis.controller.Button1b;
import com.whirvis.controller.Trigger1f;
import com.whirvis.controller.Vibration1f;
import com.whirvis.kibasan.AdapterMapping;
import com.whirvis.kibasan.DeviceAdapter;
import com.whirvis.kibasan.FeatureAdapter;
import com.whirvis.kibasan.psx.Ps3Controller;
import org.usb4java.*;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * An adapter which maps input for a DualShock 3 USB input device.
 */
public class Ds3UsbAdapter extends DeviceAdapter<Ps3Controller>
		implements TransferCallback {

	private static final byte CONFIG = 0x00;
	private static final byte ENDPOINT_IN = (byte) 0x81;
	

	private static final byte REQUEST_TYPE = (byte) 0x21;
	private static final byte REQUEST = (byte) 0x09;
	private static final byte INDEX = (byte) 0x00;
	
	private static final short SETUP_VALUE = (short) 0x03F4;
	private static final short REPORT_VALUE = (short) 0x0201;

	private static final byte[] SETUP = {
			0x42, 0x0C, 0x00, 0x00
	};

	private static final byte[] HID_REPORT = {
			(byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0xFF, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0xFF, (byte) 0x27, (byte) 0x10, (byte) 0x00, (byte) 0x32,
			(byte) 0xFF, (byte) 0x27, (byte) 0x10, (byte) 0x00, (byte) 0x32,
			(byte) 0xFF, (byte) 0x27, (byte) 0x10, (byte) 0x00, (byte) 0x32,
			(byte) 0xFF, (byte) 0x27, (byte) 0x10, (byte) 0x00, (byte) 0x32,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00
	};

	/* @formatter: off */
	@AdapterMapping
	public static final Ds3ButtonMapping
			SELECT = new Ds3ButtonMapping(Ps3Controller.SELECT, 2, 0),
			THUMB_L = new Ds3ButtonMapping(Ps3Controller.THUMB_L, 2, 1),
			THUMB_R = new Ds3ButtonMapping(Ps3Controller.THUMB_R, 2, 2),
			START = new Ds3ButtonMapping(Ps3Controller.START, 2, 3),
			UP = new Ds3ButtonMapping(Ps3Controller.UP, 2, 4),
			DOWN = new Ds3ButtonMapping(Ps3Controller.DOWN, 2, 6),
			RIGHT = new Ds3ButtonMapping(Ps3Controller.RIGHT, 2, 5),
			LEFT = new Ds3ButtonMapping(Ps3Controller.LEFT, 2, 7),
			L2 = new Ds3ButtonMapping(Ps3Controller.L2, 3, 0),
			R2 = new Ds3ButtonMapping(Ps3Controller.R2, 3, 1),
			L1 = new Ds3ButtonMapping(Ps3Controller.L1, 3, 2),
			R1 = new Ds3ButtonMapping(Ps3Controller.R1, 3, 3),
			SQUARE = new Ds3ButtonMapping(Ps3Controller.SQUARE, 3, 7),		
			TRIANGLE = new Ds3ButtonMapping(Ps3Controller.TRIANGLE, 3, 4),
			CIRCLE = new Ds3ButtonMapping(Ps3Controller.CIRCLE, 3, 5),
			CROSS = new Ds3ButtonMapping(Ps3Controller.CROSS, 3, 6);
	
	@AdapterMapping
	public static final Ds3TriggerMapping
			LT = new Ds3TriggerMapping(Ps3Controller.LT, 18),
			RT = new Ds3TriggerMapping(Ps3Controller.RT, 19);
	
	@AdapterMapping
	public static final Ds3RumbleMapping
			RUMBLE_WEAK = new Ds3RumbleMapping(Ps3Controller.RUMBLE_WEAK, 2),
			RUMBLE_STRONG = new Ds3RumbleMapping(Ps3Controller.RUMBLE_STRONG, 4);
	/* @ formatter: on */
	
	private static ByteBuffer wrapDirectBuffer(byte[] data) {
		ByteBuffer buf = ByteBuffer.allocateDirect(data.length);
		buf.put(data);
		return buf;
	}
	
	private final DeviceHandle handle;
	private final ByteBuffer hidReport;
	private ByteBuffer input;
	
	private boolean initialized;
	private boolean connected;
	private boolean requestedData;
	private byte rumbleWeak;
	private byte rumbleStrong;

	public Ds3UsbAdapter(DeviceHandle handle) {
		this.handle = Objects.requireNonNull(handle, "handle");
		this.hidReport = wrapDirectBuffer(HID_REPORT);
		this.input = ByteBuffer.allocateDirect(64);
	}
	
	@FeatureAdapter
	public void isPressed(Ds3ButtonMapping mapping, Button1b button) {
		int bits = input.get(mapping.byteOffset) & 0xFF;
		button.pressed = (bits & (1 << mapping.bitIndex)) != 0;
	}
	
	@FeatureAdapter
	public void updateForce(Ds3TriggerMapping mapping, Trigger1f trigger) {
		int value = input.get(mapping.byteOffset) & 0xFF;
		trigger.force = (value / 255.0F);
	}

	@FeatureAdapter
	public void doRumble(Ds3RumbleMapping mapping, Vibration1f vibration) {
		if (mapping == RUMBLE_WEAK) {
			byte rumbleWeak = (byte) (vibration.force > 0.0F ? 0x01 : 0x00);
			if(rumbleWeak != this.rumbleWeak) {
				hidReport.put(mapping.byteOffset, rumbleWeak);
				this.rumbleWeak = rumbleWeak;
				this.sendReport();
			}
		} else if (mapping == RUMBLE_STRONG) {
			byte rumbleStrong = (byte) (vibration.force * 255.0F);
			if(rumbleStrong != this.rumbleStrong) {
				hidReport.put(mapping.byteOffset, rumbleStrong);
				this.rumbleStrong = rumbleStrong;
				this.sendReport();
			}
		}
	}
	
	/* TODO: LEDs */

	@Override
	public boolean isConnected() {
		return this.connected;
	}
	
	@Override
	public void processTransfer(Transfer transfer) {
		if (!handle.equals(transfer.devHandle())) {
			return; /* not our device */
		} else if (transfer.endpoint() == ENDPOINT_IN) {
			this.input = transfer.buffer();
			this.requestedData = false;
		}
		LibUsb.freeTransfer(transfer);
	}
	
	private void initDevice() {
		int result = LibUsb.claimInterface(handle, CONFIG);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException(result);
		}

		ByteBuffer setup = wrapDirectBuffer(SETUP);
		int transferred = LibUsb.controlTransfer(handle, REQUEST_TYPE,
				REQUEST, SETUP_VALUE, INDEX, setup, 0L);
		if (transferred < 0) {
			throw new LibUsbException(transferred);
		}
		
		this.initialized = true;
		this.connected = true;
	}
	
	private void sendReport() {
		int result = LibUsb.controlTransfer(handle, REQUEST_TYPE, REQUEST,
				REPORT_VALUE, INDEX, hidReport, 0L);
		if (result < 0) {
			throw new LibUsbException(result);
		}
	}

	@Override
	public void poll() {
		if(!initialized) {
			this.initDevice();
		} else if(!this.isConnected()) {
			return; /* no device present */
		}
		
		/*
		 * The USB device code makes use of asynchronous IO. As such, it must
		 * ask LibUsb to handle the events manually. If this is not done, no
		 * data will come in for transfers!
		 */
		int result = LibUsb.handleEventsTimeout(null, 0);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException(result);
		}
		
		/*
		 * The requestedData boolean is used to prevent needless transfers to
		 * through the USB pipe (in hopes of increasing performance.) It is set
		 * to true when data has been requested. When input data has arrived, the
		 * handler will set requestedData to false again.
		 */
		if(!requestedData) {
			Transfer transfer = LibUsb.allocTransfer();
			ByteBuffer input = ByteBuffer.allocateDirect(64);
			LibUsb.fillInterruptTransfer(transfer, handle, ENDPOINT_IN, input, this, null, 0);
			
			result = LibUsb.submitTransfer(transfer);
			if(result == LibUsb.ERROR_IO) {
				this.connected = false;
			} else if (result != LibUsb.SUCCESS) {
				throw new LibUsbException(result);
			}
			
			this.requestedData = true;
		}
	}

}
