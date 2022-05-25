package io.ketill.psx;

import io.ketill.IoDeviceSeeker;

class MockPsxSeeker extends IoDeviceSeeker<PsxController> {

    @Override
    protected void seekImpl() {
        /* nothing to seek */
    }

}
