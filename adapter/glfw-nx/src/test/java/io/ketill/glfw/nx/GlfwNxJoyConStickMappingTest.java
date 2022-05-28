package io.ketill.glfw.nx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlfwNxJoyConStickMappingTest {

    @Test
    void testInit() {
        /*
         * It would not make sense for a JoyCon stick mapping to be created
         * with a non-existant GLFW button. As such, assume this was a user
         * mistake and throw an exception.
         */
        assertThrows(IndexOutOfBoundsException.class,
                () -> new GlfwNxJoyConStickMapping(-1, 0, 0, 0, 0));
        assertThrows(IndexOutOfBoundsException.class,
                () -> new GlfwNxJoyConStickMapping(0, -1, 0, 0, 0));
        assertThrows(IndexOutOfBoundsException.class,
                () -> new GlfwNxJoyConStickMapping(0, 0, -1, 0, 0));
        assertThrows(IndexOutOfBoundsException.class,
                () -> new GlfwNxJoyConStickMapping(0, 0, 0, -1, 0));
        assertThrows(IndexOutOfBoundsException.class,
                () -> new GlfwNxJoyConStickMapping(0, 0, 0, 0, -1));
    }

}
