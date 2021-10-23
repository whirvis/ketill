package org.ardenus.engine.input.device.adapter.gamecube;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbInterface;
import javax.usb.UsbPipe;
import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import javax.usb.event.UsbPipeListener;

/**
 * An implementation of the official USB adapter for the Wii U and Nintendo
 * Switch. The purpose of this class is to communicate with the USB adapter and
 * provide the input data necessary for a {@link GcUsbAdapter}
 * to method.
 * 
 * @see #isAdapter(UsbDevice)
 * @see #getAdapters()
 */
public class GcUsbDevice implements UsbPipeListener {

	/* @formatter: off */
	private static final short
			VENDOR_ID = 0x057E,
			PRODUCT_ID = 0x0337;

	private static final byte
			CONFIG = (byte) 0x00,
			ENDPOINT_IN = (byte) 0x81,
			ENDPOINT_OUT = (byte) 0x02;
	
	private static final byte
			RUMBLE_ID = 0x11,
			DATA_ID = 0x21;
	
	private static final byte
			RUMBLE_STOP = 0x00,
			RUMBLE_START = 0x01;
			/* RUMBLE_STOP_HARD = 0x02; */
	
	private static final byte[]
			INIT_PACKET = new byte[] { 0x13 },
			DATA_PACKET = new byte[37];
	/* @formatter: on */

	/**
	 * @param device
	 *            the USB device to check.
	 * @return {@code true} if {@code device} is a USB GameCube adapter,
	 *         {@code false} otherwise.
	 */
	public static boolean isAdapter(UsbDevice device) {
		if (device == null || device.isUsbHub()) {
			return false;
		}
		UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
		return desc.idVendor() == VENDOR_ID && desc.idProduct() == PRODUCT_ID;
	}

	private final UsbDevice device;
	private boolean initialized;
	private UsbInterface usbi;
	private UsbPipe in, out;

	private final GcUsbAdapter[] adapters;
	private final List<GcUsbAdapter> adapterList;
	private final Queue<UsbException> usbExceptions;
	private boolean requestedSlots;
	private final byte[][] slots;
	private final byte[] rumble;

	/**
	 * @param device
	 *            the USB GameCube adapter device.
	 * @throws NullPointerException
	 *             if {@code device} is {@code null}.
	 * @throws IllegalArgumentException
	 *             if {@code device} is a USB GameCube adapter according to
	 *             {@link #isAdapter(UsbDevice)}.
	 */
	public GcUsbDevice(UsbDevice device) {
		this.device = Objects.requireNonNull(device, "device");
		if (!isAdapter(device)) {
			throw new IllegalArgumentException("not a USB GameCube adapter");
		}

		this.usbExceptions = new LinkedList<>();
		this.adapters = new GcUsbAdapter[4];
		for (int i = 0; i < adapters.length; i++) {
			this.adapters[i] = new GcUsbAdapter(this, i);
		}
		this.adapterList =
				Collections.unmodifiableList(Arrays.asList(adapters));

		this.slots = new byte[adapters.length][9];
		this.rumble = new byte[1 + adapters.length];
		this.rumble[0] = RUMBLE_ID;
	}

	/**
	 * @return the adapters of each GameCube controller.
	 */
	public List<GcUsbAdapter> getAdapters() {
		return this.adapterList;
	}

	/**
	 * @param slot
	 *            the adapter slot.
	 * @return the current input data for {@code slot}.
	 */
	protected byte[] getSlot(int slot) {
		return this.slots[slot];
	}

	private UsbPipe openPipe(byte address) throws UsbException {
		UsbEndpoint endpoint = usbi.getUsbEndpoint(address);
		UsbPipe pipe = endpoint.getUsbPipe();
		pipe.open();
		return pipe;
	}

	private void initAdapter() throws UsbException {
		if (initialized) {
			throw new IllegalStateException("already initialized");
		}

		UsbConfiguration config = device.getActiveUsbConfiguration();
		this.usbi = config.getUsbInterface(CONFIG);

		usbi.claim();
		this.in = this.openPipe(ENDPOINT_IN);
		this.out = this.openPipe(ENDPOINT_OUT);

		in.addUsbPipeListener(this);
		out.syncSubmit(INIT_PACKET);
		this.initialized = true;
	}

	@Override
	public void dataEventOccurred(UsbPipeDataEvent event) {
		int offset = 0;
		byte[] packet = event.getData();
		byte id = packet[offset++];

		if (id == DATA_ID) {
			for (int i = 0; i < slots.length; i++) {
				byte[] slot = this.slots[i];
				for (int j = 0; j < slot.length; j++) {
					slot[j] = packet[offset++];
				}
			}

			for (GcUsbAdapter adapter : adapters) {
				adapter.poll();
			}
			this.requestedSlots = false;
		}
	}

	@Override
	public void errorEventOccurred(UsbPipeErrorEvent event) {
		usbExceptions.add(event.getUsbException());
	}

	private void updateRumble() throws UsbException {
		boolean submit = false;

		for (int i = 0; i < adapters.length; i++) {
			int offset = i + 1; /* RUMBLE_ID */
			boolean rumbling = adapters[i].isRumbling();
			byte state = (byte) (rumbling ? RUMBLE_START : RUMBLE_STOP);
			if (rumble[offset] != state) {
				rumble[offset] = state;
				submit = true;
			}
		}

		/*
		 * Only write to the adapter once the rumble packet has been modified.
		 * It would be horrendous for performance to send these signals every
		 * update call.
		 */
		if (submit) {
			out.asyncSubmit(rumble);
		}
	}

	/**
	 * Polling the adapter is necessary for retrieving up to date input
	 * information. If this is not done, it is possible a mix of both up to date
	 * and out of date input data will be returned. As such, it is recommended
	 * to call this method once every update.
	 * 
	 * @throws UsbException
	 *             if a USB error occurs.
	 */
	public void poll() throws UsbException {
		/*
		 * If any errors occurred outside of the polling method, go ahead and
		 * throw them here. They are not inconsequential, and should be seen by
		 * the developer.
		 */
		if (!usbExceptions.isEmpty()) {
			throw usbExceptions.remove();
		}

		if (!initialized) {
			this.initAdapter();
		}

		if (!requestedSlots) {
			in.asyncSubmit(DATA_PACKET);
			this.requestedSlots = true;
		}

		this.updateRumble();
	}

}
