package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import io.ketill.pressable.PressableIoFeatureObserver;
import org.jetbrains.annotations.NotNull;

final class AnalogTriggerObserver
        extends PressableIoFeatureObserver<TriggerStateZ> {

    private final AnalogTrigger trigger;

    AnalogTriggerObserver(@NotNull AnalogTrigger trigger,
                          @NotNull TriggerStateZ internalState,
                          @NotNull IoDeviceObserver observer) {
        super(trigger, internalState, observer);
        this.trigger = trigger;
    }

    @Override
    protected boolean isPressedImpl() {
        return AnalogTrigger.isPressed(internalState.calibratedForce);
    }

    @Override
    protected void onPress() {
        this.onNext(new AnalogTriggerPressEvent(device, trigger));
    }

    @Override
    protected void onHold() {
        this.onNext(new AnalogTriggerHoldEvent(device, trigger));
    }

    @Override
    protected void onRelease() {
        this.onNext(new AnalogTriggerReleaseEvent(device, trigger));
    }

    @Override
    public void poll() {
        super.poll();
        internalState.pressed = this.isPressed();
        internalState.held = this.isHeld();
    }

}
