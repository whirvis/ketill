package io.ketill.xinput;

import com.github.strikerx3.jxinput.XInputButtons;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
interface XInputButton {

    boolean isPressed(@NotNull XInputButtons buttons);

}
