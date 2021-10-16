package org.ardenus.engine.input.device.feature.monitor;

public abstract class PressableState {

	private static final long HOLD_TIME = 1000L;
	private static final long AUTO_PRESS = 100L;

	protected boolean pressed;
	protected long pressTime;
	protected boolean held;
	protected long lastHeldPress;
	
	protected abstract boolean isPressed();

	protected abstract void onPress(boolean held);

	protected abstract void onHold();

	protected abstract void onRelease(boolean held);

	public void update(long currentTime) {
		boolean pressed = this.isPressed();
		boolean wasPressed = this.pressed;

		if (!wasPressed && pressed) {
			this.onPress(false);
			this.pressTime = currentTime;
		} else if (wasPressed && !pressed) {
			this.onRelease(held);
			this.held = false;
		}

		this.pressed = pressed;
		if (pressed) {
			if (!held && currentTime - pressTime >= HOLD_TIME) {
				this.onHold();
				this.held = true;
			}

			if (held && currentTime - lastHeldPress >= AUTO_PRESS) {
				this.onPress(true);
				this.lastHeldPress = currentTime;
			}
		}
	}

}
