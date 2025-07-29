package kr.toxicity.model.api.data.raw;

import com.google.gson.annotations.SerializedName;
import kr.toxicity.model.api.animation.Timed;
import kr.toxicity.model.api.util.interpolation.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

/**
 * A keyframe of model.
 * @param channel channel
 * @param dataPoints movement
 * @param bezierLeftTime bezier left time
 * @param bezierLeftValue bezier left value
 * @param bezierRightTime bezier right time
 * @param bezierRightValue bezier right value
 * @param interpolation interpolation type
 * @param time keyframe time
 */
@ApiStatus.Internal
public record ModelKeyframe(
        @NotNull KeyframeChannel channel,
        @NotNull @SerializedName("data_points") List<Datapoint> dataPoints,
        @SerializedName("bezier_left_time") @Nullable Float3 bezierLeftTime,
        @SerializedName("bezier_left_value") @Nullable Float3 bezierLeftValue,
        @SerializedName("bezier_right_time") @Nullable Float3 bezierRightTime,
        @SerializedName("bezier_right_value") @Nullable Float3 bezierRightValue,
        @Nullable String interpolation,
        float time
) implements Timed {

    public @NotNull VectorInterpolation findInterpolation() {
        if (interpolation == null) return VectorInterpolation.defaultInterpolation();
        return switch (interpolation.toLowerCase()) {
            case "linear" -> LinearInterpolation.INSTANCE;
            case "catmullrom" -> CatmullRomInterpolation.INSTANCE;
            case "step" -> StepInterpolation.INSTANCE;
            case "bezier" -> new BezierInterpolation(
                    toBezier(bezierLeftTime),
                    toBezier(bezierLeftValue),
                    toBezier(bezierRightTime),
                    toBezier(bezierRightValue)
            );
            default -> VectorInterpolation.defaultInterpolation();
        };
    }

    private static @Nullable Vector3f toBezier(@Nullable Float3 float3) {
        return float3 != null ? float3.toVector() : null;
    }
}
