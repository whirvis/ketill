package io.ketill.controller;

import org.jetbrains.annotations.NotNull;

final class DeviceButtonMonitor {

    private final Controller controller;
    private final DeviceButton feature;
    private final Button1bc state;

    private boolean pressed;
    private boolean held;
    private long pressTime;
    private long lastHeldPress;

    DeviceButtonMonitor(@NotNull Controller controller,
                        @NotNull DeviceButton button) {
        this.controller = controller;
        this.feature = button;
        this.state = controller.getState(button);
    }

    boolean isButtonHeld() {
        return this.held;
    }

    private void fireEvent(DeviceButtonEvent event) {
        DeviceButtonCallback<? super Controller> callback =
                controller.buttonCallback;
        if (callback != null) {
            callback.execute(controller, feature, event, held);
        }
    }

    private void firePressEvents(long currentTime) {
        boolean pressed = state.isPressed();
        boolean wasPressed = this.pressed;
        this.pressed = pressed;

        if (!wasPressed && pressed) {
            this.pressTime = currentTime;
            this.fireEvent(DeviceButtonEvent.PRESS);
        } else if (wasPressed && !pressed) {
            this.held = false;
            this.fireEvent(DeviceButtonEvent.RELEASE);
        }
    }

    private void fireHoldEvents(long currentTime) {
        if (!pressed || !controller.isHoldEnabled()) {
            return;
        }

        long holdTime = controller.getHoldTime();
        long holdPressInterval = controller.getHoldPressInterval();

        long pressDuration = currentTime - pressTime;
        if (!held && pressDuration >= holdTime) {
            this.held = true;
            this.fireEvent(DeviceButtonEvent.HOLD);
        }

        long lastHeldPressDuration = currentTime - lastHeldPress;
        if (held && lastHeldPressDuration >= holdPressInterval) {
            this.lastHeldPress = currentTime;
            this.fireEvent(DeviceButtonEvent.PRESS);
        }
    }

    void fireEvents(long currentTime) {
        this.firePressEvents(currentTime);
        this.fireHoldEvents(currentTime);
    }

}
