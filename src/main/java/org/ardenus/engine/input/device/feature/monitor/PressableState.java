package org.ardenus.engine.input.device.feature.monitor;

public abstract class PressableState {

	protected boolean pressed;
	protected long pressTime;
	protected boolean held;
	protected long lastHeldPress;

	protected abstract void onPress(boolean held);

	protected abstract void onHold();

	protected abstract void onRelease(boolean held);

	protected void update(long currentTime, boolean pressed) {
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
			if (!held && currentTime - pressTime >= 1000L) {
				this.onHold();
				this.held = true;
			}

			if (held && currentTime - lastHeldPress >= 100L) {
				this.onPress(true);
				this.lastHeldPress = currentTime;
			}
		}
	}

	protected abstract void update(long currentTime);

}
