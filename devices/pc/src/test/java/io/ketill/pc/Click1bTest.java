package io.ketill.pc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Click1bTest {

    private Click1b click;

    @BeforeEach
    void setup() {
        this.click = new Click1b();
    }

    @Test
    void clicked() {
        click.clicked = true;
        assertTrue(click.clicked());
        click.clicked = false;
        assertFalse(click.clicked());
    }

}
