package kr.toxicity.model.api.data.blueprint;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public record AnimationMovement(
        long time,
        @Nullable Vector3f transform,
        @Nullable Vector3f scale,
        @Nullable Vector3f rotation
) implements Comparable<AnimationMovement> {

    @Override
    public int compareTo(@NotNull AnimationMovement o) {
        return Long.compare(time, o.time);
    }

    public @NotNull AnimationMovement set(long newTime) {
        if (newTime == time) return this;
        if (newTime == 0 || time == 0) return this;
        var mul = (float) newTime / (float) time;
        return new AnimationMovement(
                newTime,
                transform != null ? new Vector3f(transform)
                        .mul(mul) : null,
                scale != null ? new Vector3f(scale)
                        .mul(mul) : null,
                rotation != null ? new Vector3f(rotation)
                        .mul(mul) : null
        );
    }

    public @NotNull AnimationMovement time(long newTime) {
        return new AnimationMovement(
                newTime,
                transform,
                scale,
                rotation
        );
    }

    public @NotNull AnimationMovement plus(@NotNull AnimationMovement other) {
        return new AnimationMovement(
                time + other.time,
                plus(transform, other.transform),
                plus(scale, other.scale),
                plus(rotation, other.rotation)
        );
    }
    public @NotNull AnimationMovement minus(@NotNull AnimationMovement other) {
        return new AnimationMovement(
                time - other.time(),
                minus(transform, other.transform),
                minus(scale, other.scale),
                minus(rotation, other.rotation)
        );
    }

    public @NotNull AnimationMovement copy() {
        return new AnimationMovement(
                time,
                transform != null ? new Vector3f(transform) : null,
                scale != null ? new Vector3f(scale) : null,
                rotation != null ? new Vector3f(rotation) : null
        );
    }
    public @NotNull AnimationMovement copyNotNull() {
        return new AnimationMovement(
                time,
                transform != null ? new Vector3f(transform) : new Vector3f(),
                scale != null ? new Vector3f(scale) : new Vector3f(),
                rotation != null ? new Vector3f(rotation) : new Vector3f()
        );
    }

    private @Nullable Vector3f plus(@Nullable Vector3f one, @Nullable Vector3f two) {
        if (one != null && two != null) {
            return new Vector3f(one).add(two);
        } else if (one != null) return one;
        else return two;
    }
    private @Nullable Vector3f minus(@Nullable Vector3f one, @Nullable Vector3f two) {
        if (one != null && two != null) {
            return new Vector3f(one).sub(two);
        } else if (one != null) return one;
        else return two;
    }
}
