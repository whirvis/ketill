package io.ketill.glfw;

import com.google.gson.JsonIOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class JsonDeviceGuidsTest {

    private static InputStream getResource(String path) throws IOException {
        String fullPath = "/io/ketill/glfw/" + path;
        URL url = JsonDeviceGuidsTest.class.getResource(fullPath);
        if (url == null) {
            throw new IOException("no such resource " + fullPath);
        }
        return url.openStream();
    }

    private static void assertResourceThrows(String path) {
        assertThrows(JsonIOException.class, () -> {
            InputStream in = getResource(path);
            JsonDeviceGuids.load(in);
            in.close(); /* technically redundant */
        });
    }

    private static DeviceGuids loadValid() throws IOException {
        /* use loadResource() here for coverage */
        String path = "/io/ketill/glfw/valid.json";
        return JsonDeviceGuids.loadResource(path);
    }

    @SuppressWarnings("SameParameterValue")
    private static File copyResource(String path) throws IOException {
        InputStream in = getResource(path);

        /* generate random name for temporary file */
        Random random = new Random();
        String name = Integer.toHexString(random.nextInt());
        File tmp = new File(name + ".tmp");
        FileOutputStream tmpOut = new FileOutputStream(tmp);

        /* copying in chunks is faster */
        int read;
        byte[] buf = new byte[1024];
        while ((read = in.read(buf)) > 0) {
            tmpOut.write(buf, 0, read);
        }

        /* prevent memory leak */
        in.close();
        tmpOut.close();

        return tmp;
    }

    @Test
    void testLoad() throws IOException {
        /*
         * It would not make sense to attempt loading a JSON device GUIDs
         * container from a null input stream. As such, assume this was a
         * mistake by the user and thrown an exception.
         */
        assertThrows(NullPointerException.class,
                () -> JsonDeviceGuids.load(null));

        /*
         * The root JSON element is expected to be a JSON object. This enables
         * GSON to load the contained "guids" object and convert it to a Java
         * map instance.
         */
        assertResourceThrows("illegal_root_type.json");

        /*
         * If the GUIDs container has no systems... what is it doing here?
         * This was likely user oversight. Throw an exception in hopes of
         * preventing an annoying bug.
         */
        assertResourceThrows("no_systems.json");

        /*
         * Each system must contain one or more GUIDs for the device. For
         * simplicity, require each system store their GUIDs in an array.
         */
        assertResourceThrows("illegal_system_type.json");

        /*
         * If the system has no specified GUIDs... what is it doing here?
         * This was likely a user oversight. Throw an exception in hope of
         * preventing an annoying bug.
         */
        assertResourceThrows("no_device_guids.json");

        /*
         * Each element in this JSON array must be a string. Different
         * value types indicates the user likely has a misunderstanding
         * of how GUIDs are stored.
         */
        assertResourceThrows("illegal_guid_type.json");

        /*
         * Now that each invalid scenario has been confirmed to throw an
         * exception (as it should), ensure that a valid JSON device GUID
         * container does not cause an exception to be thrown.
         */
        DeviceGuids guids = loadValid();

        /*
         * When a requested system is not present in the JSON device
         * GUIDs container, null must be returned. This is to comply
         * with DeviceGuids specs.
         */
        assertNull(guids.getGuids("ios"));
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testLoadOnWindows() throws IOException {
        DeviceGuids guids = loadValid();

        List<String> windowsGuids = new ArrayList<>();
        windowsGuids.add("03000000ad1b000016f0000000000000");
        windowsGuids.add("030000000d0f00006300000000000000");
        windowsGuids.add("030000005e040000130b000000000000");

        assertIterableEquals(windowsGuids, guids.getSystemGuids());
    }

    @Test
    @EnabledOnOs(OS.MAC)
    void testLoadMacOnOSX() throws IOException {
        DeviceGuids guids = loadValid();

        List<String> macGuids = new ArrayList<>();
        macGuids.add("030000005e0400008e02000000000000");
        macGuids.add("030000005e040000130b000001050000");
        macGuids.add("030000005e040000200b000011050000");

        assertIterableEquals(macGuids, guids.getSystemGuids());
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void testLoadOnLinux() throws IOException {
        DeviceGuids guids = loadValid();

        List<String> linuxGuids = new ArrayList<>();
        linuxGuids.add("030000005e0400008e02000010010000");
        linuxGuids.add("030000005e0400000a0b000005040000");
        linuxGuids.add("030000005e040000120b000001050000");

        assertIterableEquals(linuxGuids, guids.getSystemGuids());
    }

    @Test
    void testLoadResource() {
        /*
         * It would not make sense to attempt loading a JSON device GUIDs
         * container from a null resource. Assume this was a mistake by the
         * user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> JsonDeviceGuids.loadResource(null));

        /*
         * However, if the resource does not exist, an IOException should be
         * thrown. NullPointerException is not thrown in this instance as it
         * leads the user to think a variable is null (when in reality a
         * resource is not present).
         */
        assertThrows(IOException.class, () -> JsonDeviceGuids.loadResource(
                "not_exist.json"));

        assertDoesNotThrow(JsonDeviceGuidsTest::loadValid);
    }

    @Test
    void testLoadFile() throws IOException {
        /*
         * It would not make sense to attempt loading a JSON device GUIDs
         * container from a null file. As such, assume this was a mistake
         * by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> JsonDeviceGuids.loadFile(null));

        File copiedFile = copyResource("valid.json");
        assertDoesNotThrow(() -> JsonDeviceGuids.loadFile(copiedFile));

        /* ensure file is deleted before exiting */
        assertTrue(copiedFile.delete());
    }

}