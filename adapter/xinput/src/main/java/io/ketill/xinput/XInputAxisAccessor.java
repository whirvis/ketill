package io.ketill.xinput;

import com.github.strikerx3.jxinput.XInputAxes;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
interface XInputAxisAccessor {

    float get(@NotNull XInputAxes axes);

}
