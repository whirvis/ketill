package io.ketill.controller;

class Analog {

    static float normalize(float value, float upper, float lower) {
        float capped = Math.min(Math.max(value, upper), lower);
        float middle = (lower - upper) / 2.0F;
        return (capped - upper - middle) / middle;
    }

}
