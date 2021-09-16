package org.ardenus.engine.input.button;

/**
 * A container for the state of a {@link Pressable} instance.
 * <p>
 * When used, instances of this container should not be accessible to outside
 * sources. If returned directly, the state could also be modified rather than
 * only read. As such, getter methods should be used to return only what need be
 * exposed to outside sources, in a read only form. E.G.,
 * {@code isPressed(Pressable)}, {@code getPressTime(Pressable)}, etc.
 */
public class PressableState implements Cloneable {

	/* TODO: add callbacks for state changes */
	
	public static final long INACTIVE = -1L;

	private boolean pressed;
	private boolean held;

	private long pressTime_ms;
	private long releaseTime_ms;
	private long holdPressTime_ms;

	private PressableState prev;

	public PressableState() {
		this.pressTime_ms = INACTIVE;
		this.releaseTime_ms = INACTIVE;
		this.holdPressTime_ms = INACTIVE;
	}

	/**
	 * Sets whether or not the state is pressed.
	 * <p>
	 * Setting the state with this function will not update other respective
	 * fields. The state must first be cached via {@link #cache()}, modified,
	 * and then updated {@link #update()} for the desired effect.
	 * 
	 * @param pressed
	 *            {@code true} if the state is pressed, {@code false} otherwise.
	 */
	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	/**
	 * Returns if the state is set to pressed.
	 * 
	 * @return {@code true} if the state is currently set to pressed,
	 *         {@code false} otherwise.
	 */
	public boolean isPressed() {
		return this.pressed;
	}

	/**
	 * Returns if the state is considered to be held down.
	 * <p>
	 * The state is considered to be held down if it has been pressed for a
	 * duration of {@code 1000ms} or longer.
	 * 
	 * @return {@code true} if the state is considered to be held down,
	 *         {@code false} otherwise.
	 */
	public boolean isHeld() {
		return this.held;
	}

	/**
	 * Returns the time the state was set to pressed.
	 * 
	 * @return the time in milliseconds the state was set to pressed,
	 *         {@value #INACTIVE} if it is not currently pressed.
	 */
	public long getPressTimeMs() {
		return this.pressTime_ms;
	}

	/**
	 * Returns the time the state was set to released.
	 * <p>
	 * If the button has never been pressed, then this function will act as
	 * though the state was never set to released.
	 * 
	 * @return the time in milliseconds the state was set to released,
	 *         {@value #INACTIVE} if it is not currently released.
	 */
	public long getReleaseTimeMs() {
		return this.releaseTime_ms;
	}

	/**
	 * Caches this pressable state via {@link #clone()} so that it can later be
	 * updated via {@link #update()}. This works by calling {@code cache()},
	 * updating the state of this pressable (e.g., setting {@code pressed} to
	 * {@code true}), and then calling {@code update()}. This will have other
	 * fields like {@code pressTime_ms} updated automatically, as well as
	 * relevant callback methods called.
	 */
	public void cache() {
		this.prev = this.clone();
	}

	/**
	 * Updates the state based on the last call to {@link #cache()}.
	 * <p>
	 * After calling this method, the cache will be cleared. This means the
	 * state must be cached before every update.
	 * 
	 * @throws IllegalStateException
	 *             if a call to {@code cache()} has yet to be made.
	 */
	public void update() {
		if (prev != null) {
			throw new IllegalStateException("must first call cache()");
		}

		long currentTime_ms = System.currentTimeMillis();

		if (!prev.pressed && this.pressed) {
			this.pressTime_ms = currentTime_ms;
			this.releaseTime_ms = INACTIVE;
		} else if (prev.pressed && !this.pressed) {
			this.held = false;
			this.pressTime_ms = INACTIVE;
			this.releaseTime_ms = currentTime_ms;
			this.holdPressTime_ms = INACTIVE;
		}

		if (!this.held && this.pressed) {
			long holdTime_ms = currentTime_ms - pressTime_ms;
			if (holdTime_ms >= 1000L) {
				this.held = true;
			}
		}

		if (this.held) {
			long pressWait_ms = currentTime_ms - holdPressTime_ms;
			if (pressWait_ms >= 100L) {
				this.holdPressTime_ms = currentTime_ms;
			}
		}

		/*
		 * If the cache is not cleared later calls to this method will result in
		 * erroneous data and responses.
		 */
		this.prev = null;
	}

	@Override
	protected PressableState clone() {
		try {
			return (PressableState) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
