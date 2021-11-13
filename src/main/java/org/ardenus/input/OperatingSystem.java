package org.ardenus.input;

public enum OperatingSystem {

	WINDOWS("windows", "Windows", false, "win"),
	OSX("osx", "Mac OSX", true, "mac", "darwin"),
	LINUX("linux", "Linux", true, "linux", "ubuntu"),
	SOLARIS("solaris", "Solaris", true, "solaris", "sun"),

	ANDROID("android", "Android", true),
	IOS("ios", "IOS", true);

	public final String id;
	public final String name;
	public final boolean isUnix;
	private final String[] identifiers;

	private OperatingSystem(String id, String name, boolean isUnix,
			String... identifiers) {
		this.id = id;
		this.name = name;
		this.isUnix = isUnix;
		this.identifiers = identifiers;
	}

	/**
	 * @param osName
	 *            the name of the system.
	 * @return {@code true} if {@code systemName} signifies it is this kind of
	 *         operating system, {@code false} otherwise.
	 */
	public boolean isSystem(String osName) {
		if (osName == null) {
			return false;
		}

		osName = osName.toLowerCase();
		for (String identifier : identifiers) {
			if (osName.contains(identifier)) {
				return true;
			}
		}
		return false;
	}

	private static boolean determinedOs;
	private static OperatingSystem currentOs;

	/**
	 * @return the operating system this machine is running on, {@code null} if
	 *         it could not be determined.
	 */
	public static OperatingSystem get() {
		if (determinedOs) {
			return currentOs;
		}

		String osName = System.getProperty("os.name");
		for (OperatingSystem os : OperatingSystem.values()) {
			if (os.isSystem(osName)) {
				currentOs = os;
				break;
			}
		}
		determinedOs = true;
		return currentOs;
	}

}
