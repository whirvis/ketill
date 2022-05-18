package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import io.ketill.pressable.PressableIoFeatureObserver;
import org.jetbrains.annotations.NotNull;

final class AnalogTriggerObserver extends PressableIoFeatureObserver<TriggerStateZ> {

    private final @NotNull AnalogTrigger trigger;

    AnalogTriggerObserver(@NotNull AnalogTrigger trigger,
                          @NotNull TriggerStateZ internalState,
                          @NotNull IoDeviceObserver observer) {
        super(trigger, internalState, observer);
        this.trigger = trigger; /* prevent casting */
    }

    @Override
    protected boolean isPressed() {
        return AnalogTrigger.isPressed(internalState.calibratedForce);
    }

    @Override
    protected void onPress() {
        internalState.pressed = true;
        this.onNext(new AnalogTriggerPressEvent(device, trigger));
    }

    @Override
    protected void onHold() {
        internalState.held = true;
        this.onNext(new AnalogTriggerHoldEvent(device, trigger));
    }

    @Override
    protected void onRelease() {
        internalState.pressed = false;
        internalState.held = false;
        this.onNext(new AnalogTriggerReleaseEvent(device, trigger));
    }

}
