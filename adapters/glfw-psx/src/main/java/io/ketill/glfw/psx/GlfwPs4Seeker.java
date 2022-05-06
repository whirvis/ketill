package io.ketill.glfw.psx;

import io.ketill.glfw.GlfwJoystickSeeker;
import io.ketill.psx.Ps4Controller;
import io.ketill.psx.PsxAmbiguityCallback;
import io.ketill.psx.PsxAmbiguityCandidate;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class GlfwPs4Seeker extends GlfwJoystickSeeker<Ps4Controller>
        implements PsxAmbiguityCandidate<Ps4Controller> {

    private boolean ambiguous;
    private @Nullable PsxAmbiguityCallback<Ps4Controller> ambiguityCallback;

    /**
     * Since both USB and Bluetooth controllers can connect, there exists
     * a possibility a single PlayStation 4 controller will report itself
     * as both a USB controller and a Bluetooth controller.
     *
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException     if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @see #isAmbiguous()
     * @see #onAmbiguity(PsxAmbiguityCallback)
     */
    public GlfwPs4Seeker(long ptr_glfwWindow) {
        super(Ps4Controller.class, ptr_glfwWindow);

        String guidsPath = "guids_ps4.json";
        Collection<String> ps4Guids = this.loadJsonGuids(guidsPath);
        this.wrangleGuids(ps4Guids, GlfwPs4Adapter::wrangle);
    }

    @Override
    public final boolean isAmbiguous() {
        return this.ambiguous;
    }

    @Override
    public final void onAmbiguity(@Nullable PsxAmbiguityCallback<Ps4Controller> callback) {
        this.ambiguityCallback = callback;
    }

    private void checkAmbiguity() {
        boolean nowAmbiguous = this.getDeviceCount() > 1;
        if (!this.ambiguous && nowAmbiguous) {
            if (ambiguityCallback != null) {
                ambiguityCallback.execute(this, true);
            }
            this.ambiguous = true;
        } else if (this.ambiguous && !nowAmbiguous) {
            if (ambiguityCallback != null) {
                ambiguityCallback.execute(this, false);
            }
            this.ambiguous = false;
        }
    }

    @Override
    @MustBeInvokedByOverriders
    protected void deviceDiscovered(@NotNull Ps4Controller controller) {
        this.checkAmbiguity();
    }

    @Override
    @MustBeInvokedByOverriders
    protected void deviceForgotten(@NotNull Ps4Controller controller) {
        this.checkAmbiguity();
    }

}
