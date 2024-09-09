package kiinse.dev.telegram.configuration;

import lombok.NonNull;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

public class Config {

    private final Logger logger = LoggerFactory.getLogger("Config");
    public TomlParseResult config;
    public boolean isDebug;

    public Config() {
        config = getToml("config.toml");
        isDebug = Objects.requireNonNullElse(config.getBoolean("debug"), false);
    }

    private @NonNull TomlParseResult getToml(@NonNull String fileName) {
        val file = new File("configs", fileName);
        if (!file.exists()) {
            val inputStream = accessFile(fileName);
            if (inputStream != null) {
                try {
                    FileUtils.copyInputStreamToFile(inputStream, file);
                } catch ( Exception e) {
                    logger.warn("Error while copying config file {}", fileName, e);
                }
            } else {
                logger.warn("File '{}' not found inside jar.", fileName);
            }
        }
        try (final InputStream is = Files.newInputStream(file.toPath())) {
            val result = Toml.parse(is);
            for (val error : result.errors()) {
                logger.warn(error.getMessage());
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> @NonNull T get(@NonNull String key, @NonNull T defaultValue) {
        val value = config.get(key);
        if (value != null) {
            try {
                return (T) value;
            } catch (Exception e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public <T> @Nullable T get(@NonNull String key) {
        val value = config.get(key);
        if (value != null) {
            try {
                return (T) value;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private @Nullable InputStream accessFile(String file) {
        val stream = this.getClass().getResourceAsStream(file);
        if (stream != null) { return stream; }
        return this.getClass().getClassLoader().getResourceAsStream(file);
    }

}
