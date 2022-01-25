package com.whirvis.kibasan.glfw;

import com.whirvis.kibasan.InputDevice;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class GlfwJoystickSeeker<I extends InputDevice>
        extends GlfwDeviceSeeker<I> {

    private static final int JOYSTICK_COUNT = GLFW_JOYSTICK_LAST + 1;

    private final I[] joysticks;
    private final Map<String, GlfwJoystickWrangler<I>> wranglers;

    /**
     * @param type           the joystick type.
     * @param ptr_glfwWindow the GLFW window pointer.
     * @see #seekGuid(String, GlfwJoystickWrangler)
     */
    @SuppressWarnings("unchecked")
    public GlfwJoystickSeeker(@NotNull Class<I> type, long ptr_glfwWindow) {
        super(ptr_glfwWindow);
        this.joysticks = (I[]) Array.newInstance(type, JOYSTICK_COUNT);
        this.wranglers = new HashMap<>();
    }

    /**
     * @param guid the joystick GUID, case-sensitive.
     * @return {@code true} if this device seeker is seeking out joysticks with
     * the specified GUID, {@code false} otherwise.
     */
    public boolean isSeeking(@NotNull String guid) {
        return wranglers.containsKey(guid);
    }

    /**
     * Binds the GUID of a joystick to a joystick wrangler. When a joystick
     * is detected, its GUID will be checked to see if the seeker should
     * register it. When a joystick should be registered, {@code wrangler}
     * will be invoked to create the device, so it can be discovered.
     *
     * @param guid     the joystick GUID, case-sensitive.
     * @param wrangler the joystick wrangler.
     */
    public void seekGuid(@NotNull String guid,
                         @NotNull GlfwJoystickWrangler<I> wrangler) {
        wranglers.put(guid, wrangler);
    }

    /**
     * Binds the GUID of a joystick to a joystick wrangler. When a joystick
     * is detected, its GUID will be checked to see if the seeker should
     * register it. When a joystick should be registered, {@code wrangler}
     * will be invoked to create the device, so it can be discovered.
     * <p/>
     * This method is a shorthand for
     * {@link #seekGuid(String, GlfwJoystickWrangler)}, with each element of
     * {@code guids} being passed as the argument for {@code guid}.
     *
     * @param guids    the joystick GUIDs, case-sensitive.
     * @param wrangler the joystick wrangler.
     */
    public void seekGuids(@NotNull Iterable<@NotNull String> guids,
                          @NotNull GlfwJoystickWrangler<I> wrangler) {
        for (String guid : guids) {
            this.seekGuid(guid, wrangler);
        }
    }

    /**
     * All currently registered joysticks with a matching GUID will be
     * automatically unregistered. This is to prevent the connection of
     * undesired joysticks from lingering.
     *
     * @param toDrop the GUIDs to drop, case-sensitive.
     */
    public void dropGuids(@NotNull Collection<@NotNull String> toDrop) {
        /*
         * Since access to the joysticks array is not synchronized, the
         * seekImpl() method could be invoked while this method is still
         * being executed. If this method forgets a device while seekImpl()
         * is still running, the joystick could be rediscovered in error.
         *
         * To prevent this from occurring, simply remove the associated
         * wrangler from each GUID to drop before forgetting them. This
         * ensures seekImpl() will not immediately rediscover them.
         */
        for (String guid : toDrop) {
            wranglers.remove(guid);
        }

        /*
         * If a joystick's GUID matches one of the GUIDs being dropped, it must
         * be unregistered. It would not make logical sense for a joystick that
         * would no longer be registered by this seeker to linger.
         */
        for (int i = 0; i < joysticks.length; i++) {
            String guid = glfwGetJoystickGUID(i);
            if (guid != null && toDrop.contains(guid)) {
                this.forgetDevice(joysticks[i]);
                this.joysticks[i] = null;
            }
        }
    }

    /**
     * All currently registered joysticks with a matching GUID will be
     * automatically unregistered. This is to prevent the connection of
     * undesired joysticks from lingering.
     * <p/>
     * This method is a shorthand for {@link #dropGuids(Collection)}, with
     * {@code toDrop} being passed as a collection via
     * {@link Collections#singletonList(Object)}.
     *
     * @param toDrop the GUID to drop, case-sensitive.
     */
    public void dropGuid(@NotNull String toDrop) {
        this.dropGuids(Collections.singletonList(toDrop));
    }

    @Override
    public void seekImpl() {
        for (int i = 0; i < joysticks.length; i++) {
            I joystick = this.joysticks[i];
            if (joystick != null) {
                if (!joystick.isConnected()) {
                    this.forgetDevice(joystick);
                    this.joysticks[i] = null;
                }
                continue;
            }

            /*
             * If the joystick is not present, glfwGetJoystickGUID() returns
             * null. This makes a call to glfwJoystickPresent() redundant.
             */
            String guid = glfwGetJoystickGUID(i);
            if (guid != null && this.isSeeking(guid)) {
                GlfwJoystickWrangler<I> wrangler = wranglers.get(guid);
                this.joysticks[i] = wrangler.wrangleDevice(ptr_glfwWindow, i);
                this.discoverDevice(joysticks[i]);
            }
        }
    }

}
