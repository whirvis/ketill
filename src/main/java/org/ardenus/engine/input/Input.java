package org.ardenus.engine.input;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.whirvex.event.EventManager;

/**
 * The input system for the Ardenus Engine.
 * 
 * @see #init(EventManager)
 */
public class Input {

	protected static final Logger LOG = LogManager.getLogger(Input.class);

	private static boolean initialized;
	private static EventManager events;

	/**
	 * Initializes the input system.
	 * 
	 * @param eventManager
	 *            the event manager, may be {@code null}.
	 * @see #sendEvent(InputEvent)
	 */
	public static void init(EventManager eventManager) {
		if (initialized == true) {
			LOG.error("Already initialized");
			return;
		}

		events = EventManager.valueOf(eventManager);

		initialized = true;
		LOG.info("Initalized system");
	}

	protected static void requireInit() {
		if (initialized == false) {
			throw new IllegalStateException("not initialized");
		}
	}

	/**
	 * Sends an {@link InputEvent} to the input system's event manager.
	 * 
	 * @param <T>
	 *            the event type.
	 * @param event
	 *            the input event.
	 * @return {@code event} as passed.
	 * @throws IllegalStateException
	 *             if the input system is not initialized.
	 * @throws NullPointerException
	 *             if {@code event} is {@code null}.
	 */
	public static <T extends InputEvent> T sendEvent(T event) {
		Input.requireInit();
		return events.send(event);
	}

}
