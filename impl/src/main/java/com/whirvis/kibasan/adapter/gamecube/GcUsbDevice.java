package com.whirvis.kibasan.adapter.gamecube;

import org.usb4java.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An implementation of the official Nintendo GameCube USB adapter for the Wii U
 * and Nintendo Switch. Its purpose is to communicate with the adapter and
 * provide the input data necessary for a {@link GcUsbAdapter} to function.
 *
 * @see #isAdapter(Device)
 * @see #getAdapters()
 */
public class GcUsbDevice implements TransferCallback {

	/* @formatter: off */
	public static final short
			VENDOR_ID = 0x057E,
			PRODUCT_ID = 0x0337;

	private static final byte
			CONFIG = (byte) 0x00,
			ENDPOINT_IN = (byte) 0x81,
			ENDPOINT_OUT = (byte) 0x02;
	
	private static final byte
			RUMBLE_ID = 0x11,
			INIT_ID = 0x13,
			DATA_ID = 0x21;
	
	private static final byte
			RUMBLE_STOP = 0x00,
			RUMBLE_START = 0x01;
		 /* RUMBLE_STOP_HARD = 0x02; */

	private static final int
			DATA_LEN = 37;
	/* @formatter: on */

	/**
	 * @param device
	 *            the USB device to check.
	 * @return {@code true} if {@code device} is a GameCube USB adapter,
	 *         {@code false} otherwise.
	 */
	public static boolean isAdapter(Device device) {
		if (device == null) {
			return false;
		}
		DeviceDescriptor desc = new DeviceDescriptor();
		LibUsb.getDeviceDescriptor(device, desc);
		return desc.idVendor() == VENDOR_ID && desc.idProduct() == PRODUCT_ID;
	}

	/**
	 * @param handle
	 *            the handle of the USB device to check.
	 * @return {@code true} if {@code handle} is for a GameCube USB adapter
	 *         device, {@code false} otherwise.
	 */
	public static boolean isAdapter(DeviceHandle handle) {
		if (handle == null) {
			return false;
		}
		return isAdapter(LibUsb.getDevice(handle));
	}

	private final DeviceHandle handle;
	private boolean initialized;

	private final GcUsbAdapter[] adapters;
	private final List<GcUsbAdapter> adapterList;
	private boolean requestedSlots;
	private final byte[][] slots;
	private final ByteBuffer rumble;

	/**
	 * @param handle
	 *            the GameCube USB adapter device.
	 * @throws NullPointerException
	 *             if {@code device} is {@code null}.
	 * @throws IllegalArgumentException
	 *             if {@code device} is not a GameCube USB adapter according to
	 *             {@link #isAdapter(Device)}.
	 */
	public GcUsbDevice(DeviceHandle handle) {
		this.handle = Objects.requireNonNull(handle, "handle");
		if (!isAdapter(handle)) {
			throw new IllegalArgumentException("not a USB GameCube adapter");
		}

		/*
		 * This is cached into an unmodifiable list so as to prevent many calls
		 * to getAdapters() from creating a many collections on the heap if
		 * called numerous times.
		 */
		this.adapters = new GcUsbAdapter[4];
		for (int i = 0; i < adapters.length; i++) {
			this.adapters[i] = new GcUsbAdapter(this, i);
		}
		this.adapterList =
				Collections.unmodifiableList(Arrays.asList(adapters));

		this.slots = new byte[adapters.length][9];
		this.rumble = ByteBuffer.allocateDirect(1 + adapters.length);
		rumble.put(RUMBLE_ID);
	}

	public List<GcUsbAdapter> getAdapters() {
		return this.adapterList;
	}

	protected byte[] getSlotData(int slot) {
		return this.slots[slot];
	}

	private void initAdapter() {
		if (initialized) {
			throw new IllegalStateException("already initialized");
		}

		int result = LibUsb.claimInterface(handle, CONFIG);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException(result);
		}

		ByteBuffer init = ByteBuffer.allocateDirect(1);
		init.put(INIT_ID);
		IntBuffer transferred = IntBuffer.allocate(1);
		result = LibUsb.interruptTransfer(handle, ENDPOINT_OUT, init,
				transferred, 0);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException(result);
		}

		this.initialized = true;
	}

	private void handlePacket(ByteBuffer packet) {
		byte id = packet.get();
		if (id == DATA_ID) {
			for (int i = 0; i < slots.length; i++) {
				byte[] slot = this.slots[i];
				for (int j = 0; j < slot.length; j++) {
					slot[j] = packet.get();
				}
			}

			for (GcUsbAdapter adapter : adapters) {
				adapter.poll();
			}
			this.requestedSlots = false;
		}
	}

	private void updateRumble() {
		boolean submit = false;

		for (int i = 0; i < adapters.length; i++) {
			int offset = i + 1; /* account for rumble ID */
			boolean rumbling = adapters[i].isRumbling();
			byte state = (byte) (rumbling ? RUMBLE_START : RUMBLE_STOP);
			if (rumble.get(offset) != state) {
				rumble.put(offset, state);
				submit = true;
			}
		}

		/*
		 * Only write to the adapter once the rumble packet has been modified.
		 * It would be horrendous for performance to send these signals every
		 * update call.
		 */
		if (submit) {
			Transfer transfer = LibUsb.allocTransfer();
			LibUsb.fillInterruptTransfer(transfer, handle, ENDPOINT_OUT, rumble,
					this, null, 0);
			int result = LibUsb.submitTransfer(transfer);
			if (result != LibUsb.SUCCESS) {
				throw new LibUsbException(result);
			}
		}
	}

	@Override
	public void processTransfer(Transfer transfer) {
		if (!handle.equals(transfer.devHandle())) {
			return; /* not our device */
		} else if (transfer.endpoint() == ENDPOINT_IN) {
			this.handlePacket(transfer.buffer());
		}
		LibUsb.freeTransfer(transfer);
	}

	/**
	 * Polling the adapter is necessary for retrieving up to date input
	 * information. If this is not done, it is possible a mix of both up to date
	 * and out of date input data will be returned. It is recommended to call
	 * this method once every update.
	 * 
	 * @throws LibUsbException
	 *             if an error in LibUSB occurs.
	 */
	public void poll() {
		if (!initialized) {
			this.initAdapter();
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
		 * The requestedSlots boolean is used to prevent needless transfers to
		 * through the USB pipe (in hopes of increasing performance.) It is set
		 * to true when data has been requested. When slot data has arrived, the
		 * handler will set requestedSlots to false again.
		 */
		if (!requestedSlots) {
			Transfer transfer = LibUsb.allocTransfer();
			ByteBuffer data = ByteBuffer.allocateDirect(DATA_LEN);
			LibUsb.fillInterruptTransfer(transfer, handle, ENDPOINT_IN, data,
					this, null, 0L);

			result = LibUsb.submitTransfer(transfer);
			if (result != LibUsb.SUCCESS) {
				throw new LibUsbException(result);
			}

			this.requestedSlots = true;
		}

		this.updateRumble();
	}

	public void shutdown() {
		if (!initialized) {
			throw new IllegalStateException("not initialized");
		}

		/* zero out all slot data to prevent false positives */
		for (int i = 0; i < slots.length; i++) {
			byte[] data = this.slots[i];
			for (int j = 0; j < data.length; j++) {
				data[j] = 0x00;
			}
		}
		
		/* tell controllers to stop rumbling if possible */
		try {
			this.updateRumble();
		} catch(Exception e) {
			/* oh well, we tried */
		}

		this.initialized = false;
	}

}
