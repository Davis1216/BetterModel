package kr.toxicity.model.api.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Pack util
 */
@ApiStatus.Internal
public final class PackUtil {
    /**
     * No initializer
     */
    private PackUtil() {
        throw new RuntimeException();
    }

    private static final Pattern REPLACE_SOURCE = Pattern.compile("[^a-z0-9_.]");

    /**
     * Converts some path to compatible with Minecraft resource location
     * @param raw raw path
     * @return converted path
     */
    public static @NotNull String toPackName(@NotNull String raw) {
        return REPLACE_SOURCE.matcher(raw.toLowerCase()).replaceAll("_");
    }
}
