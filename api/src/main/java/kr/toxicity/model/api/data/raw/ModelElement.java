package kr.toxicity.model.api.data.raw;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record ModelElement(
        @NotNull UUID uuid,
        @NotNull Float3 from,
        @NotNull Float3 to,
        @Nullable Float3 rotation,
        @NotNull Float3 origin,
        @NotNull ModelFace faces
) {
    public @NotNull Float3 size() {
        return to.minus(from);
    }
    public float max() {
        return size().max();
    }
    public boolean hasTexture() {
        return faces.hasTexture();
    }
}
