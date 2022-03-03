package io.ketill.glfw;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A JSON based container for the GUIDs of a device.
 * <p>
 * These JSON containers consist of one or more JSON arrays. The arrays in
 * question contain the GUIDs of a device when connected to a specific OS.
 * The keys of these arrays correspond to the operating system ID.
 * <p>
 * The following is an example of a JSON container:
 * <pre>
 * {
 *   "windows": [
 *     "03000000ad1b000016f0000000000000",
 *     "030000000d0f00006300000000000000",
 *     "030000005e040000130b000000000000"
 *   ],
 *   "mac_osx": [
 *     "030000005e0400008e02000000000000",
 *     "030000005e040000130b000001050000",
 *     "030000005e040000200b000011050000"
 *   ],
 *   "linux": [
 *     "030000005e0400008e02000010010000",
 *     "030000005e0400000a0b000005040000",
 *     "030000005e040000120b000001050000"
 *   ]
 * }
 * </pre>
 */
public class JsonDeviceGuids extends DeviceGuids {

    /**
     * Loads the device GUIDs of a JSON file from an input stream.
     * <p>
     * For this method to successfully return, {@code in} must contain a
     * valid JSON device GUIDs container. If the contained JSON does not
     * follow specifications, a {@code JsonIOException} will be thrown.
     * <p>
     * An example of a valid container is given in the class JavaDocs.
     *
     * @param in the input stream to read from.
     * @return the loaded device GUIDs.
     * @throws NullPointerException if {@code in} is {@code null}.
     * @throws JsonIOException      if {@code in} contains invalid JSON
     *                              for a JSON device GUIDs container.
     */
    public static @NotNull DeviceGuids load(@NotNull InputStream in) {
        Objects.requireNonNull(in, "in");

        JsonDeviceGuids guids = new JsonDeviceGuids();
        InputStreamReader isr = new InputStreamReader(in);

        /*
         * The root JSON element is expected to be a JSON object.
         * This is so GSON can load the contained "guids" object
         * and convert it to a Java map instance.
         */
        JsonElement json = JsonParser.parseReader(isr);
        if (!json.isJsonObject()) {
            throw new JsonIOException("expected JSON object");
        }

        /*
         * If the GUIDs container has no systems... what is it
         * doing here? This was likely user oversight. Throw an
         * exception in hopes of preventing an annoying bug.
         */
        JsonObject jsonObj = json.getAsJsonObject();
        if (jsonObj.entrySet().isEmpty()) {
            throw new JsonIOException("no systems present");
        }

        for (String systemId : jsonObj.keySet()) {
            /*
             * Each system must contain one or more GUIDs for the
             * device. For simplicity, require each system store
             * their GUIDs in a JSON array.
             */
            JsonElement systemGuids = jsonObj.get(systemId);
            if (!systemGuids.isJsonArray()) {
                throw new JsonIOException("\"" + systemId + "\" must be a " + "JSON array");
            }

            /*
             * If the system has no specified GUIDs... what is it
             * doing here? This was likely a user oversight. Throw
             * an exception in hope of preventing an annoying bug.
             */
            JsonArray systemGuidsArr = systemGuids.getAsJsonArray();
            if (systemGuidsArr.isEmpty()) {
                throw new JsonIOException("\"" + systemId + "\" has no " +
                        "device" + " GUIDs");
            }

            /*
             * Each element in this JSON array must be a string.
             * Different value types indicates the user likely
             * has a misunderstanding of how GUIDs are stored.
             */
            List<String> systemGuidsList = new ArrayList<>();
            for (int i = 0; i < systemGuidsArr.size(); i++) {
                JsonElement guid = systemGuidsArr.get(i);
                if (!guid.isJsonPrimitive() || !guid.getAsJsonPrimitive().isString()) {
                    throw new JsonIOException("element " + i + " of \"" + systemId + "\" must be a string");
                }
                systemGuidsList.add(guid.getAsString());
            }

            guids.guids.put(systemId, systemGuidsList);
        }

        return guids;
    }

    /**
     * Loads device GUIDs from a JSON file in the classpath.
     * <p>
     * For this method to successfully return, the resource must contain
     * a valid JSON device GUIDs container. If the JSON contained in the
     * resource does not follow specifications, a {@code JsonIOException}
     * will be thrown.
     * <p>
     * An example of a valid container is given in the class JavaDocs.
     *
     * @param path the path of the resource to read from.
     * @return the loaded device GUIDs.
     * @throws NullPointerException if {@code path} is {@code null}.
     * @throws JsonIOException      if the resource at {@code path}
     *                              contains invalid JSON for a JSON
     *                              device GUIDs container.
     * @throws IOException          if an I/O error occurs.
     */
    /* @formatter:off */
    public static @NotNull DeviceGuids
            loadResource(@NotNull String path) throws IOException {
        Objects.requireNonNull(path, "path");
        URL url = DeviceGuids.class.getResource(path);
        if (url == null) {
            throw new IOException("no such resource " + path);
        }
        return load(url.openStream());
    }
    /* @formatter:on */

    /**
     * Loads device GUIDs from a JSON file.
     * <p>
     * For this method to successfully return, the contents of {@code file}
     * must contain a valid JSON device GUIDs container. If the contents
     * of the file do not follow specifications, a {@code JsonIOException}
     * will be thrown.
     * <p>
     * An example of a valid container is given in the class JavaDocs.
     *
     * @param file the file to read from.
     * @return the loaded device GUIDs.
     * @throws NullPointerException if {@code file} is {@code null}.
     * @throws JsonIOException      if the contents of {@code file}
     *                              contains invalid JSON for a JSON
     *                              device GUIDs container.
     * @throws IOException          if an I/O error occurs.
     */
    /* @formatter:off */
    public static @NotNull DeviceGuids
            loadFile(@NotNull File file) throws IOException {
        Objects.requireNonNull(file, "file");

        FileInputStream in = new FileInputStream(file);
        DeviceGuids guids = load(in);
        in.close(); /* prevent memory leak */

        return guids;
    }
    /* @formatter:on */

    /**
     * Loads device GUIDs from a JSON file.
     * <p>
     * For this method to successfully return, the file at {@code path} must
     * contain a valid JSON device GUIDs container. If the file's contents do
     * not follow specifications, a {@code JsonIOException} will be thrown.
     * <p>
     * An example of a valid container is given in the class JavaDocs.
     *
     * @param path the path of the file to read from.
     * @return the loaded device GUIDs.
     * @throws NullPointerException if {@code path} is {@code null}.
     * @throws JsonIOException      if the contents of the file located
     *                              {@code path} contains invalid JSON
     *                              for a JSON device GUIDs container.
     * @throws IOException          if an I/O error occurs.
     */
    /* @formatter:off */
    public static @NotNull DeviceGuids
            loadFile(@NotNull String path) throws IOException {
        Objects.requireNonNull(path, "path");
        return loadFile(new File(path));
    }
    /* @formatter:on */

    private final Map<String, List<String>> guids;

    private JsonDeviceGuids() {
        this.guids = new HashMap<>();
    }

    /* @formatter:off */
    @Override
    protected @Nullable Collection<@NotNull String>
            getGuidsImpl(@NotNull String systemId) {
        List<String> osGuids = guids.get(systemId);
        if (osGuids != null && !osGuids.isEmpty()) {
            return Collections.unmodifiableList(osGuids);
        }
        return null;
    }
    /* @formatter: on */

}
