package com.whirvis.kibasan.psx;

import com.whirvis.kibasan.AdapterSupplier;
import com.whirvis.kibasan.AnalogStick;
import com.whirvis.kibasan.AnalogTrigger;
import com.whirvis.kibasan.Button1bc;
import com.whirvis.kibasan.Controller;
import com.whirvis.kibasan.DeviceButton;
import com.whirvis.kibasan.Direction;
import com.whirvis.kibasan.FeaturePresent;
import com.whirvis.kibasan.FeatureState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A Sony PlayStation controller.
 */
public abstract class PsxController extends Controller {

    /* @formatter:off */
    @FeaturePresent
    public static final @NotNull DeviceButton
            BUTTON_SQUARE = new DeviceButton("square"),
            BUTTON_CROSS = new DeviceButton("cross"),
            BUTTON_CIRCLE = new DeviceButton("circle"),
            BUTTON_TRIANGLE = new DeviceButton("triangle"),
            BUTTON_L1 = new DeviceButton("l1"),
            BUTTON_R1 = new DeviceButton("r1"),
            BUTTON_L2 = new DeviceButton("l2"),
            BUTTON_R2 = new DeviceButton("r2"),
            BUTTON_L_THUMB = new DeviceButton("l_thumb"),
            BUTTON_R_THUMB = new DeviceButton("r_thumb"),
            BUTTON_UP = new DeviceButton("up", Direction.UP),
            BUTTON_RIGHT = new DeviceButton("right", Direction.RIGHT),
            BUTTON_DOWN = new DeviceButton("down", Direction.DOWN),
            BUTTON_LEFT = new DeviceButton("left", Direction.LEFT);

    @FeaturePresent
    public static final @NotNull AnalogStick
            STICK_LS = new AnalogStick("ls", BUTTON_L_THUMB),
            STICK_RS = new AnalogStick("rs", BUTTON_R_THUMB);
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState
    public final @NotNull Button1bc
            square = this.getState(BUTTON_SQUARE),
            cross = this.getState(BUTTON_CROSS),
            circle = this.getState(BUTTON_CIRCLE),
            triangle = this.getState(BUTTON_TRIANGLE),
            l1 = this.getState(BUTTON_L1),
            r1 = this.getState(BUTTON_R1),
            l2 = this.getState(BUTTON_L2),
            r2 = this.getState(BUTTON_R2),
            lThumb = this.getState(BUTTON_L_THUMB),
            rThumb = this.getState(BUTTON_R_THUMB),
            up = this.getState(BUTTON_UP),
            right = this.getState(BUTTON_RIGHT),
            down = this.getState(BUTTON_DOWN),
            left = this.getState(BUTTON_RIGHT);
    /* @formatter:on */

    public PsxController(@NotNull String id,
                         @NotNull AdapterSupplier<?> adapterSupplier,
                         @Nullable AnalogTrigger lt,
                         @Nullable AnalogTrigger rt) {
        super(id, adapterSupplier, STICK_LS, STICK_RS, lt, rt);
    }

}
