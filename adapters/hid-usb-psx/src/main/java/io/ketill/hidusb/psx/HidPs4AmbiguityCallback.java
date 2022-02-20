package io.ketill.hidusb.psx;

@FunctionalInterface
public interface HidPs4AmbiguityCallback {

    void execute(boolean ambiguous);

}
