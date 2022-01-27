package com.whirvis.kibasan.nx;

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

/**
 * A Nintendo Switch Pro controller.
 */
public class NxProController extends Controller {

    /* @formatter:off */
    @FeaturePresent
    public static final @NotNull DeviceButton
            BUTTON_B = new DeviceButton("b"),
            BUTTON_A = new DeviceButton("a"),
            BUTTON_Y = new DeviceButton("y"),
            BUTTON_X = new DeviceButton("x"),
            BUTTON_L = new DeviceButton("l"),
            BUTTON_R = new DeviceButton("r"),
            BUTTON_ZL = new DeviceButton("zl"),
            BUTTON_ZR = new DeviceButton("zr"),
            BUTTON_MINUS = new DeviceButton("minus"),
            BUTTON_PLUS = new DeviceButton("plus"),
            BUTTON_L_THUMB = new DeviceButton("l_thumb"),
            BUTTON_R_THUMB = new DeviceButton("r_thumb"),
            BUTTON_HOME = new DeviceButton("home"),
            BUTTON_SCREENSHOT = new DeviceButton("screenshot"),
            BUTTON_BUMPER = new DeviceButton("bumper"),
            BUTTON_Z_BUMPER = new DeviceButton("z_bumper"),
            BUTTON_UP = new DeviceButton("up", Direction.UP),
            BUTTON_RIGHT = new DeviceButton("right", Direction.RIGHT),
            BUTTON_DOWN = new DeviceButton("down", Direction.DOWN),
            BUTTON_LEFT = new DeviceButton("left", Direction.LEFT);

    @FeaturePresent
    public static final @NotNull AnalogStick
            STICK_LS = new AnalogStick("ls", BUTTON_L_THUMB),
            STICK_RS = new AnalogStick("rs", BUTTON_R_THUMB);

    @FeaturePresent
    public static final @NotNull AnalogTrigger
            TRIGGER_LT = new AnalogTrigger("lt"),
            TRIGGER_RT = new AnalogTrigger("rt");
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState
    public final @NotNull Button1bc
            b = this.getState(BUTTON_B),
            a = this.getState(BUTTON_A),
            y = this.getState(BUTTON_Y),
            x = this.getState(BUTTON_X),
            l = this.getState(BUTTON_L),
            r = this.getState(BUTTON_R),
            zl = this.getState(BUTTON_ZL),
            zr = this.getState(BUTTON_ZR),
            minus = this.getState(BUTTON_MINUS),
            plus = this.getState(BUTTON_PLUS),
            lThumb = this.getState(BUTTON_L_THUMB),
            rThumb = this.getState(BUTTON_R_THUMB),
            home = this.getState(BUTTON_HOME),
            screenshot = this.getState(BUTTON_SCREENSHOT),
            bumper = this.getState(BUTTON_BUMPER),
            zBumper = this.getState(BUTTON_Z_BUMPER),
            up = this.getState(BUTTON_UP),
            right = this.getState(BUTTON_RIGHT),
            down = this.getState(BUTTON_DOWN),
            left = this.getState(BUTTON_LEFT);
    /* @formatter:on */

    public NxProController(@NotNull AdapterSupplier<NxProController> adapterSupplier) {
        super("nx_pro", adapterSupplier, STICK_LS, STICK_RS, TRIGGER_LT,
                TRIGGER_RT);
    }

}
