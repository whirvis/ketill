package com.whirvis.kibasan.gc;

import com.whirvis.kibasan.AdapterSupplier;
import com.whirvis.kibasan.AnalogStick;
import com.whirvis.kibasan.AnalogTrigger;
import com.whirvis.kibasan.Button1bc;
import com.whirvis.kibasan.Controller;
import com.whirvis.kibasan.DeviceButton;
import com.whirvis.kibasan.Direction;
import com.whirvis.kibasan.FeaturePresent;
import com.whirvis.kibasan.FeatureState;
import com.whirvis.kibasan.RumbleMotor;
import com.whirvis.kibasan.Trigger1fc;
import com.whirvis.kibasan.Vibration1f;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3fc;

/**
 * A Nintendo GameCube controller.
 */
public class GcController extends Controller {

    /* @formatter:off */
    @FeaturePresent
    public static final @NotNull DeviceButton
            BUTTON_A = new DeviceButton("a"),
            BUTTON_B = new DeviceButton("b"),
            BUTTON_X = new DeviceButton("x"),
            BUTTON_Y = new DeviceButton("y"),
            BUTTON_LEFT = new DeviceButton("left", Direction.LEFT),
            BUTTON_RIGHT = new DeviceButton("right", Direction.RIGHT),
            BUTTON_DOWN = new DeviceButton("down", Direction.DOWN),
            BUTTON_UP = new DeviceButton("up", Direction.UP),
            BUTTON_START = new DeviceButton("start"),
            BUTTON_Z = new DeviceButton("z"),
            BUTTON_R = new DeviceButton("r"),
            BUTTON_L = new DeviceButton("l");

    @FeaturePresent
    public static final @NotNull AnalogStick
            STICK_LS = new AnalogStick("ls"),
            STICK_RS = new AnalogStick("rs");

    @FeaturePresent
    public static final @NotNull AnalogTrigger
            TRIGGER_LT = new AnalogTrigger("lt"),
            TRIGGER_RT = new AnalogTrigger("rt");

    @FeaturePresent
    public static final @NotNull RumbleMotor
            MOTOR_RUMBLE = new RumbleMotor("rumble");
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState
    public final @NotNull Button1bc
            a = this.getState(BUTTON_A),
            b = this.getState(BUTTON_B),
            x = this.getState(BUTTON_X),
            y = this.getState(BUTTON_Y),
            left = this.getState(BUTTON_LEFT),
            right = this.getState(BUTTON_RIGHT),
            down = this.getState(BUTTON_DOWN),
            up = this.getState(BUTTON_UP),
            start = this.getState(BUTTON_START),
            z = this.getState(BUTTON_Z),
            r = this.getState(BUTTON_R),
            l = this.getState(BUTTON_L);

    @FeatureState
    public final @NotNull Vector3fc
            ls = this.getState(STICK_LS),
            rs = this.getState(STICK_RS);

    @FeatureState
    public final @NotNull Trigger1fc
            lt = this.getState(TRIGGER_LT),
            rt = this.getState(TRIGGER_RT);

    @FeatureState
    public final @NotNull Vibration1f
            rumble = this.getState(MOTOR_RUMBLE);
    /* @formatter:on */

    public GcController(@NotNull AdapterSupplier<GcController> adapterSupplier) {
        super("gc", adapterSupplier, STICK_LS, STICK_RS, TRIGGER_LT,
                TRIGGER_RT);
    }

}
