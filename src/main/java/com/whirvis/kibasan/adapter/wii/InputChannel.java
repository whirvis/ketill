package com.whirvis.kibasan.adapter.wii;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * Represents an HID input channel for the Nintendo Wiimote.
 * <p>
 * Since the Nintendo Wiimote has multiple modes of input data, this dedicated
 * type has been created to facilitate a standard way to use them. This is to
 * allow users to take full advantage of the Wiimote's capabilities, adding
 * features that do not come packaged with the input system (such as the IR
 * receiver or accelerometer.)
 * 
 * @see Block
 */
public abstract class InputChannel {

	/**
	 * A block of data present within an input channel.
	 */
	public static class Block {

		public final int offset;
		public final int length;

		public Block(int offset, int length) {
			if (offset < 0) {
				throw new IllegalArgumentException("offset < 0");
			} else if (length <= 0) {
				throw new IllegalArgumentException("length <= 0");
			}
			this.offset = offset;
			this.length = length;
		}

	}

	public static final Block CORE = new Block(0, 2);

	public final int reportId;
	public final boolean delta;
	private final int reportLen;
	private final Map<Block, byte[]> blocks;

	/**
	 * @param reportId
	 *            the report ID, must fit in an {@code unsigned byte}.
	 * @param delta
	 *            {@code true} if reports should be sent on this channel only
	 *            when data has been changed, {@code false} otherwise. Depending
	 *            on the channel, this value may not heed the expected behavior.
	 * @param blocks
	 *            the blocks of input data present within this channel.
	 * @throws IllegalArgumentException
	 *             if {@code reportId} does not fit into an
	 *             {@code unsigned byte}; if {@code blocks} has no elements.
	 * @throws NullPointerException
	 *             if {@code blocks} is {@code null}.
	 * @see Block
	 */
	public InputChannel(int reportId, boolean delta, Block... blocks) {
		Objects.requireNonNull(blocks, "blocks");
		if (reportId < 0x00 || reportId > 0xFF) {
			throw new IndexOutOfBoundsException(
					"reportId < 0x00 || reportId > 0xFF");
		} else if (blocks.length <= 0) {
			throw new IllegalArgumentException("no blocks");
		}

		this.reportId = reportId;
		this.delta = delta;
		this.blocks = new HashMap<>();

		Block highestBlock = blocks[0];
		for (int i = 1; i < blocks.length; i++) {
			if (blocks[i].offset > highestBlock.offset) {
				highestBlock = blocks[i];
			}
		}

		this.reportLen = highestBlock.offset + highestBlock.length;

		for (Block channel : blocks) {
			Objects.requireNonNull(channel, "channel");
			byte[] data = new byte[channel.length];
			this.blocks.put(channel, data);
		}
	}

	public boolean hasBlock(Block block) {
		if (block != null) {
			return blocks.containsKey(block);
		}
		return false;
	}

	public byte[] getData(Block block) {
		if (block != null) {
			return blocks.get(block);
		}
		return null;
	}

	/**
	 * Handles the report by verifying the data within each block is valid. The
	 * data for each block is in turn also brought up to date.
	 * 
	 * @param report
	 *            the input report to process.
	 * @return {@code true} if the input report is valid, {@code false}
	 *         otherwise.
	 */
	public boolean handleReport(byte[] report) {
		Objects.requireNonNull(report, "report");
		if (report.length < reportLen) {
			return false; /* report is too short */
		} else if ((report[0] & 0xFF) != reportId) {
			throw new IllegalArgumentException("bad report ID");
		}

		boolean validReport = true;
		for (Entry<Block, byte[]> entry : blocks.entrySet()) {
			Block channel = entry.getKey();
			byte[] data = entry.getValue();

			int offset = channel.offset;
			offset += 1; /* account for report ID */
			for (int i = 0; i < channel.length; i++) {
				data[i] = report[offset++];
			}

			if (!this.verifyData(channel, data)) {
				validReport = false;
			}
		}
		return validReport;
	}

	/**
	 * By default, this method will always return {@code true}.
	 * 
	 * @param block
	 *            the block whose data to verify.
	 * @param data
	 *            the data to be verified.
	 * @return {@code true} if {@code data} is valid, {@code false} otherwise.
	 */
	protected boolean verifyData(Block block, byte[] data) {
		return true;
	}

}
