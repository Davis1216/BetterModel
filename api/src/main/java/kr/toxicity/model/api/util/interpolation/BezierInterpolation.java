package kr.toxicity.model.api.util.interpolation;

import kr.toxicity.model.api.animation.VectorPoint;
import kr.toxicity.model.api.util.InterpolationUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

/**
 * Bezier interpolator
 */
@RequiredArgsConstructor
@ApiStatus.Internal
public final class BezierInterpolation implements VectorInterpolation {

    private final @Nullable Vector3f bezierLeftTime, bezierLeftValue, bezierRightTime, bezierRightValue;

    @NotNull
    @Override
    public VectorPoint interpolate(@NotNull List<VectorPoint> points, int p2Index, float time) {
        var p1 = p2Index > 0 ? points.get(p2Index - 1) : VectorPoint.EMPTY;
        var p2 = points.get(p2Index);
        return new VectorPoint(
                InterpolationUtil.bezier(
                        time,
                        p1.time(),
                        p2.time(),
                        p1.vector(),
                        p2.vector(),
                        bezierLeftTime,
                        bezierLeftValue,
                        bezierRightTime,
                        bezierRightValue
                ),
                time,
                this
        );
    }
}
