package io.ketill;

public class IoDevice {

    /* placeholder */

    protected <S extends IoState<I>, I> S addFeature(IoFeature<S> feature) {
        return null;
    }

    protected <S extends IoState<?>> S getState(IoFeature<S> feature) {
        return null;
    }

    public void query() {

    }

    public void update() {

    }

}
