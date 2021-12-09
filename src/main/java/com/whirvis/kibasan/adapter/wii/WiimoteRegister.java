package com.whirvis.kibasan.adapter.wii;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

import org.hid4java.HidDevice;

/**
 * A handle for the Wiimote's register and EEPROM memory.
 * 
 * @see #read(boolean, int, int, RegisterRunnable)
 * @see #write(boolean, int, byte[])
 */
public class WiimoteRegister {

	@FunctionalInterface
	public static interface RegisterRunnable {

		/**
		 * Called when the requested data has been fully read.
		 * 
		 * @param address
		 *            the address read from.
		 * @param data
		 *            the data read.
		 */
		public void run(int address, byte[] data);

	}

	private static class ReadRequest {

		private final boolean eeprom;
		private final int addr;
		private final byte[] data;
		private final RegisterRunnable onReceive;

		private int bytesReceived;

		public ReadRequest(boolean eeprom, int addr, int size,
				RegisterRunnable onReceive) {
			this.eeprom = eeprom;
			this.addr = addr;
			this.data = new byte[size];
			this.onReceive = onReceive;
		}

	}

	private final HidDevice hid;
	private final Queue<ReadRequest> readQueue;
	private ReadRequest currentRead;

	/**
	 * @param hid
	 *            the HID device, must be open.
	 * @throws NullPointerException
	 *             if {@code hid} is {@code null}.
	 */
	public WiimoteRegister(HidDevice hid) {
		this.hid = Objects.requireNonNull(hid, "hid");
		this.readQueue = new LinkedList<>();
	}

	private void sendRequest(ReadRequest request) {
		int offset = 0;
		byte[] packet = new byte[6];

		/* whether or not to read from EEPROM */
		packet[offset++] = (byte) (request.eeprom ? 0b000 : 0b100);

		/* register address to read */
		packet[offset++] = (byte) (request.addr >> 16);
		packet[offset++] = (byte) (request.addr >> 8);
		packet[offset++] = (byte) (request.addr >> 0);

		/* amount of data to read */
		int size = request.data.length;
		packet[offset++] = (byte) (size >> 8);
		packet[offset++] = (byte) (size >> 0);

		hid.write(packet, packet.length, (byte) 0x17);
	}

	/**
	 * Queues a data read from the Wiimote.
	 * <p>
	 * This method is non-blocking. This is thanks to the {@code onReceive}
	 * parameter, which is a executed once the requested data has arrived. It
	 * runs on the same thread that {@link #handleData(byte[])} is called.
	 * <p>
	 * <b>Note:</b> To prevent the Wiimote from possibly becoming overwhelmed,
	 * reads are not sent as soon as they are requested. Rather, they are sent
	 * once the previous read (if any) has been completed. However, if there are
	 * no previous reads, then this request is sent immediately.
	 * 
	 * @param eeprom
	 *            {@code true} to read from the EEPROM, {@code false} to read
	 *            from the registers.
	 * @param address
	 *            the address to read from.
	 * @param size
	 *            the amount of data to read in bytes.
	 * @param onReceive
	 *            the code to run once the data requested has been received. A
	 *            value of {@code null} is permitted, and will result in no code
	 *            being executed.
	 * @throws IndexOutOfBoundsException
	 *             if {@code address} is less than {@code 0x000000}; if
	 *             {@code address + size} is greater than {@code 0xFFFFFF}.
	 * @throws IllegalArgumentException
	 *             if {@code size} is less than or equal to zero.
	 * @see #write(boolean, int, byte[])
	 */
	public void read(boolean eeprom, int address, int size,
			RegisterRunnable onReceive) {
		if (address < 0x000000) {
			throw new IndexOutOfBoundsException("address < 0x000000");
		} else if (address + size > 0xFFFFFF) {
			throw new IndexOutOfBoundsException("address + size > 0xFFFFFF");
		} else if (size <= 0) {
			throw new IllegalArgumentException("size <= 0");
		}

		ReadRequest request = new ReadRequest(eeprom, address, size, onReceive);
		readQueue.add(request);

		/*
		 * If the read queue only has a size of one, then immediately send this
		 * request packet. Once the data for this request has been received, the
		 * handler will send the request next in line. This prevents the Wiimote
		 * from possibly getting confused by an influx of read requests.
		 */
		if (readQueue.size() == 1) {
			this.sendRequest(request);
		}
	}

	/**
	 * Writes data to the Wiimote.
	 * <p>
	 * This method is non-blocking. If {@code data} is longer than sixteen bytes
	 * in length, it will be split up into sixteen byte chunks and sent across
	 * multiple packets (with a properly offset address.) This is done due to a
	 * Wiimote restriction which limits the amount of data that can be written
	 * at once to just sixteen bytes.
	 * 
	 * @param eeprom
	 *            {@code true} to write to the EEPROM, {@code false} to write to
	 *            the registers.
	 * @param address
	 *            the address to write to.
	 * @param data
	 *            the data to write, cannot be empty.
	 * @throws NullPointerException
	 *             if {@code data} is {@code null}.
	 * @throws IndexOutOfBoundsException
	 *             if {@code address} is less than {@code 0x000000}; if
	 *             {@code address + data.length} is greater than
	 *             {@code 0xFFFFFF}.
	 * @throws IllegalArgumentException
	 *             if {@code data} is an empty array.
	 */
	public void write(boolean eeprom, int address, byte[] data) {
		Objects.requireNonNull(data, "data");
		if (address < 0x000000) {
			throw new IndexOutOfBoundsException("address < 0x000000");
		} else if (address + data.length > 0xFFFFFF) {
			throw new IndexOutOfBoundsException(
					"address + data.length > 0xFFFFFF");
		} else if (data.length <= 0) {
			throw new IllegalArgumentException("data.length <= 0");
		}

		int dataOffset = 0;
		while (dataOffset < data.length) {
			int offset = 0;
			byte[] write = new byte[21];

			/* whether or not to write to EEPROM */
			write[offset++] = (byte) (eeprom ? 0b000 : 0b100);;

			/* register address to write */
			write[offset++] = (byte) (address >> 16);
			write[offset++] = (byte) (address >> 8);
			write[offset++] = (byte) (address >> 0);

			/* determine chunk size */
			int chunkSize = 16;
			if (chunkSize + dataOffset >= data.length) {
				chunkSize = (data.length - dataOffset);
			}

			/* write current data chunk */
			write[offset++] = (byte) chunkSize;
			for (int i = 0; i < chunkSize; i++) {
				write[offset++] = data[dataOffset++];
			}

			/*
			 * If the data being sent is larger than sixteen bytes, the Wiimote
			 * will not accept it. To get around this, multiple write packets
			 * are sent if the data being written is too large. The address is
			 * incremented by the amount of bytes written so the next write is
			 * done at the correct location.
			 */
			address += chunkSize;

			hid.write(write, write.length, (byte) 0x16);
		}
	}

	public boolean isDataReport(byte[] packet) {
		if (packet == null || packet.length < 1) {
			return false;
		}
		return packet[0] == (byte) 0x21;
	}

	/**
	 * @param packet
	 *            the packet to handle.
	 * @throws NullPointerException
	 *             if {@code packet} is {@code null}.
	 * @throws IllegalArgumentException
	 *             if {@code packet} is not a register report.
	 * @see #isDataReport(byte[])
	 */
	public void handleData(byte[] packet) {
		Objects.requireNonNull(packet, "packet");
		if (!this.isDataReport(packet)) {
			throw new IllegalArgumentException("not a register report");
		}

		ReadRequest request = this.currentRead;
		if (request == null) {
			return;
		}

		/*
		 * The Wiimote sends the data size in 4 bits, making the range of values
		 * zero to fifteen. However, the Wiimote wouldn't send an empty register
		 * data packet. This makes the minimum size actually one. This makes the
		 * range of values one to sixteen in reality. This is accounted for by
		 * simply adding one to the size after extracting it from the packet.
		 */
		int size = ((packet[3] & 0xF0) >> 4) + 1;

		/* requested data offset */
		int offset = 0;
		offset |= (byte) ((packet[4] & 0xFF) << 0);
		offset |= (byte) ((packet[5] & 0xFF) << 8);

		/* write the chunk received */
		request.bytesReceived += size;
		for (int i = 0; i < size; i++) {
			request.data[offset + i] = packet[6 + i];
			if (offset + i + 1 >= request.data.length) {
				break;
			}
		}

		/*
		 * Once the amount of data read from this request matches the length of
		 * the request data, the current read has finished. As such, run the on
		 * receive code (if any) and send the next register read request.
		 */
		if (request.bytesReceived >= request.data.length) {
			RegisterRunnable onReceive = request.onReceive;
			if (onReceive != null) {
				onReceive.run(request.addr, request.data);
			}

			if (!readQueue.isEmpty()) {
				this.currentRead = readQueue.remove();
				this.sendRequest(readQueue.peek());
			} else {
				this.currentRead = null;
			}
		}
	}

}
