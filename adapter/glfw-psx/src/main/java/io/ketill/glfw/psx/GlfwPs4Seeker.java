package io.ketill.glfw.psx;

import io.ketill.ToStringUtils;
import io.ketill.glfw.GlfwJoystickSeeker;
import io.ketill.psx.Ps4Controller;
import io.ketill.psx.PsxAmbiguityCandidate;
import io.ketill.psx.PsxAmbiguityEvent;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * A {@link Ps4Controller} seeker using GLFW.
 * <p>
 * <b>Note:</b> Since both USB and Bluetooth controllers can connect, there
 * exists a possibility a single PlayStation 4 controller will report itself
 * as both a USB controller and a Bluetooth controller.
 *
 * @see #isAmbiguous()
 * @see PsxAmbiguityEvent
 */
public class GlfwPs4Seeker extends GlfwJoystickSeeker<Ps4Controller>
        implements PsxAmbiguityCandidate {

    private boolean ambiguous;

    /**
     * Constructs a new {@code GlfwPs4Seeker}.
     *
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException if {@code ptr_glfwWindow} is a null
     *                              pointer (has a value of zero).
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

    private void checkAmbiguity() {
        boolean nowAmbiguous = this.getDeviceCount() > 1;
        if (!this.ambiguous && nowAmbiguous) {
            observer.onNext(new PsxAmbiguityEvent(this, true));
            this.ambiguous = true;
        } else if (this.ambiguous && !nowAmbiguous) {
            observer.onNext(new PsxAmbiguityEvent(this, false));
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

    /* @formatter:off */
    @Override
    public String toString() {
        return ToStringUtils.getJoiner(super.toString(), this)
                .add("ambiguous=" + ambiguous)
                .toString();
    }
    /* @formatter:on */

}
