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
    void isClicked() {
        click.clicked = true;
        assertTrue(click.isClicked());
        click.clicked = false;
        assertFalse(click.isClicked());
    }

    @Test
    void isHeld() {
        click.held = true;
        assertTrue(click.isHeld());
        click.held = false;
        assertFalse(click.isHeld());
    }

}
