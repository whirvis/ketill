package io.ketill.hidusb;

@FunctionalInterface
public interface HidPs4AmbiguityCallback {

    void execute(boolean ambiguous);

}
