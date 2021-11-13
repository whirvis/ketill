package org.ardenus.input;

import java.util.Objects;

/**
 * A container for device GUIDs.
 * 
 * @see #getGuids(String, OperatingSystem)
 */
public interface DeviceGuids {

	/**
	 * @param id
	 *            the input device ID.
	 * @param os
	 *            the operating system ID.
	 * @return the GUIDs for device with {@code id} on {@code os}, {@code null}
	 *         if none exist.
	 * @throws NullPointerException
	 *             if {@code id} or {@code os} are {@code null}.
	 */
	public Iterable<String> getGuids(String id, String os);

	/**
	 * @param id
	 *            the input device ID.
	 * @param os
	 *            the operating system.
	 * @return the GUIDs for device with {@code id} on {@code os}, {@code null}
	 *         if none exist.
	 * @throws NullPointerException
	 *             if {@code id} or {@code os} are {@code null}.
	 */
	public default Iterable<String> getGuids(String id, OperatingSystem os) {
		Objects.requireNonNull(os, "os");
		return this.getGuids(id, os.id);
	}

}
