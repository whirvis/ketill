package io.ketill.pc;

public class Click1b implements Click1bc {

    public boolean clicked;

    /**
     * @param clicked the initial click state.
     */
    public Click1b(boolean clicked) {
        this.clicked = clicked;
    }

    /**
     * Constructs a new {@code Click1b} with {@code clicked} set to
     * {@code false}.
     */
    public Click1b() {
        this(false);
    }

    @Override
    public boolean clicked() {
        return this.clicked;
    }

}
