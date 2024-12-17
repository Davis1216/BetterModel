package kr.toxicity.model.api.data.raw;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public record ModelAnimation(
        @NotNull String name,
        @NotNull UUID uuid,
        @NotNull Map<UUID, ModelAnimator> animators
) {
}
