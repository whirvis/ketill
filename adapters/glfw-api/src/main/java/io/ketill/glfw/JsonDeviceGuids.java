package io.ketill.glfw;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A JSON based container for the GUIDs of a device.
 * <p>
 * JSON containers must have a JSON object with the key {@code "guids"}.
 * This object contains arrays of GUIDs, with their key being the ID of
 * the corresponding operating system.
 * <p>
 * The following is an example of a JSON container:
 * <pre>
 * {
 *  "guids": {
 *    "windows": [
 *      "03000000ad1b000016f0000000000000",
 *      "030000000d0f00006300000000000000",
 *      "030000005e040000130b000000000000"
 *    ],
 *    "linux": [
 *      "030000005e0400008e02000010010000",
 *      "030000005e0400000a0b000005040000",
 *      "030000005e040000120b000001050000"
 *    ],
 *    "mac_osx": [
 *      "030000005e0400008e02000000000000",
 *      "030000005e040000130b000001050000",
 *      "030000005e040000200b000011050000"
 *    ]
 *  }
 * }
 * </pre>
 */
public class JsonDeviceGuids extends DeviceGuids {

    private static final Gson GSON = new GsonBuilder().create();

    /**
     * Loads the device GUIDs of a JSON file from an input stream.
     *
     * @param in the input stream to read from.
     * @return the loaded device GUIDs.
     */
    public static @NotNull DeviceGuids load(@NotNull InputStream in) {
        Objects.requireNonNull(in, "in");
        InputStreamReader isr = new InputStreamReader(in);
        JsonElement json = JsonParser.parseReader(isr);
        return GSON.fromJson(json, JsonDeviceGuids.class);
    }

    /**
     * Loads device GUIDs from a JSON file in the classpath.
     * <p>
     * This method is a shorthand for {@link #load(InputStream)}, with the
     * argument for {@code in} being the URL of {@code path} opened as an
     * input stream using {@link URL#openStream()}.
     *
     * @param path the path of the resource to read from.
     * @return the loaded device GUIDs.
     * @throws IOException if an I/O error occurs.
     */
    /* @formatter:off */
    public static @NotNull DeviceGuids
            loadResource(@NotNull String path) throws IOException {
        Objects.requireNonNull(path, "path");
        URL url = DeviceGuids.class.getResource(path);
        if (url == null) {
            throw new IOException("no such resource");
        }
        return load(url.openStream());
    }
    /* @formatter:on */

    /**
     * Loads device GUIDs from a JSON file.
     * <p>
     * This method is a shorthand for {@link #load(InputStream)}, with the
     * argument for {@code in} being {@code new FileInputStream(file)}.
     *
     * @param file the file to read from.
     * @return the loaded device GUIDs.
     * @throws IOException          if an I/O error occurs.
     * @throws NullPointerException if {@code file} is {@code null}.
     */
    /* @formatter:off */
    public static @NotNull DeviceGuids
            loadFile(@NotNull File file) throws IOException {
        Objects.requireNonNull(file, "file");
        return load(new FileInputStream(file));
    }
    /* @formatter:on */

    /**
     * Loads device GUIDs from a JSON file.
     * <p>
     * This method is a shorthand for {@link #loadFile(File)}, with the
     * argument for {@code file} being {@code new File(path)}.
     *
     * @param path the path of the file to read from.
     * @return the loaded device GUIDs.
     * @throws IOException          if an I/O error occurs.
     * @throws NullPointerException if {@code path} is {@code null}.
     */
    /* @formatter:off */
    public static @NotNull DeviceGuids
            loadFile(@NotNull String path) throws IOException {
        Objects.requireNonNull(path, "path");
        return loadFile(new File(path));
    }
    /* @formatter:on */

    /* loaded by GSON */
    @SuppressWarnings("all")
    private Map<String, Set<String>> guids;

    private JsonDeviceGuids() {
        /* require initialization via load() */
    }

    /* @formatter:off */
    @Override
    protected @Nullable Set<@NotNull String>
            getGuidsImpl(@NotNull String systemId) {
        Set<String> osGuids = guids.get(systemId);
        if (osGuids != null && !osGuids.isEmpty()) {
            return Collections.unmodifiableSet(osGuids);
        }
        return null;
    }
    /* @formatter: on */

}
