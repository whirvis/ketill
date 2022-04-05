package io.ketill.pressable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Required by {@link PressableFeatureMonitor} for I/O devices that want
 * to make use of it for their features. The pressable config should
 * initially be {@link PressableFeatureConfig#DEFAULT}.
 */
public interface PressableFeatureSupport {

    /**
     * Sets the configuration governing pressable features. These are used
     * to determine if a feature is being held down, how quickly they should
     * be virtually pressed, etc.
     *
     * @param config the configuration to use. A value of {@code null} is
     *               permitted, and results in the default configuration
     *               as specified by {@link PressableFeatureConfig#DEFAULT}
     *               being
     *               used instead.
     */
    void usePressableConfig(@Nullable PressableFeatureConfigView config);

    /**
     * @return the configuration governing pressable features. These are used
     * to determine if a feature is being held down, how quickly they should
     * be virtually pressed, etc.
     */
    @NotNull PressableFeatureConfigView getPressableConfig();

    /**
     * Sets the callback for when an event related to a pressable feature
     * occurs (e.g., when the feature is pressed or released).
     * <p>
     * <b>Note:</b> Implementing classes wishing to listen for this event
     * should  override {@link #firedPressableEvent(PressableFeatureEvent)}.
     * The callback is for users.
     *
     * @param callback the code to execute when a pressable feature related
     *                 event occurs. A value of {@code null} is permitted,
     *                 and will result in nothing being executed.
     */
    void onPressableEvent(@Nullable Consumer<PressableFeatureEvent> callback);

    /**
     * <b>This method should only be called by a
     * {@link PressableFeatureMonitor}</b>.
     * <p>
     * Called when a pressable event is fired. Overriding this method allows
     * for an I/O device to know when a pressable event has occurred without
     * needing to set themselves as the callback.
     *
     * @param event the fired event.
     */
    @SuppressWarnings("unused")
    default void firedPressableEvent(@NotNull PressableFeatureEvent event) {
        /* optional implement */
    }

}
