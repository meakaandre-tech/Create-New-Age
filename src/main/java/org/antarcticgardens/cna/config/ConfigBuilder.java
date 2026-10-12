package org.antarcticgardens.cna.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.antarcticgardens.cna.CreateNewAge;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * JSON-backed stand-in for NeoForge's ModConfigSpec builder, with the same define calls, so the config
 * classes keep their shape. Each config is one file in the config folder; sections become nested objects
 * and comments are written as "// key" entries next to their value.
 */
public class ConfigBuilder {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final List<ConfigValue<?>> values = new ArrayList<>();
    private final Deque<String> path = new ArrayDeque<>();
    private String[] pendingComment = new String[0];

    public ConfigBuilder comment(String... lines) {
        pendingComment = lines;
        return this;
    }

    public ConfigBuilder push(String section) {
        path.addLast(section);
        return this;
    }

    public ConfigBuilder pop() {
        path.removeLast();
        return this;
    }

    public ConfigValue<Integer> defineInRange(String key, int defaultValue, int min, int max) {
        return add(new ConfigValue<>(List.copyOf(path), key, take(), defaultValue, (double) min, (double) max));
    }

    public ConfigValue<Double> defineInRange(String key, double defaultValue, double min, double max) {
        return add(new ConfigValue<>(List.copyOf(path), key, take(), defaultValue, min, max));
    }

    public ConfigValue<Boolean> define(String key, boolean defaultValue) {
        return add(new ConfigValue<>(List.copyOf(path), key, take(), defaultValue, null, null));
    }

    private String[] take() {
        String[] comment = pendingComment;
        pendingComment = new String[0];
        return comment;
    }

    private <T> ConfigValue<T> add(ConfigValue<T> value) {
        values.add(value);
        return value;
    }

    /** Reads the file if it exists, then writes it back with every value, so new options appear. */
    public void load(Path file) {
        JsonObject json = new JsonObject();
        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file)) {
                JsonElement parsed = JsonParser.parseReader(reader);
                if (parsed.isJsonObject())
                    json = parsed.getAsJsonObject();
            } catch (Exception e) {
                CreateNewAge.LOGGER.warn("Could not read config {}, using defaults", file, e);
            }
        }

        JsonObject out = new JsonObject();
        for (ConfigValue<?> value : values) {
            value.read(section(json, value.path, false));
            value.write(section(out, value.path, true));
        }

        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(out, writer);
            }
        } catch (Exception e) {
            CreateNewAge.LOGGER.warn("Could not write config {}", file, e);
        }
    }

    private static JsonObject section(JsonObject root, List<String> path, boolean create) {
        JsonObject current = root;
        for (String name : path) {
            if (current.has(name) && current.get(name).isJsonObject()) {
                current = current.getAsJsonObject(name);
            } else if (create) {
                JsonObject child = new JsonObject();
                current.add(name, child);
                current = child;
            } else {
                return new JsonObject();
            }
        }
        return current;
    }

    public static final class ConfigValue<T> {
        private final List<String> path;
        private final String key;
        private final String[] comment;
        private final T defaultValue;
        private final Double min;
        private final Double max;
        private T value;

        private ConfigValue(List<String> path, String key, String[] comment, T defaultValue, Double min, Double max) {
            this.path = path;
            this.key = key;
            this.comment = comment;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
            this.min = min;
            this.max = max;
        }

        public T get() {
            return value;
        }

        public T getDefault() {
            return defaultValue;
        }

        @SuppressWarnings("unchecked")
        private void read(JsonObject json) {
            if (!json.has(key))
                return;
            try {
                JsonElement e = json.get(key);
                if (defaultValue instanceof Boolean) {
                    value = (T) (Boolean) e.getAsBoolean();
                } else if (defaultValue instanceof Integer) {
                    value = (T) (Integer) (int) clamp(e.getAsInt());
                } else if (defaultValue instanceof Double) {
                    value = (T) (Double) clamp(e.getAsDouble());
                }
            } catch (Exception ex) {
                CreateNewAge.LOGGER.warn("Invalid config value for {}, using {}", key, defaultValue);
                value = defaultValue;
            }
        }

        private double clamp(double v) {
            if (min != null)
                v = Math.max(min, v);
            if (max != null)
                v = Math.min(max, v);
            return v;
        }

        private void write(JsonObject json) {
            if (comment.length > 0)
                json.addProperty("// " + key, String.join(" ", comment));
            if (value instanceof Boolean b)
                json.addProperty(key, b);
            else if (value instanceof Number n)
                json.addProperty(key, n);
        }
    }
}
