package kr.toxicity.model.api.tracker;

import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.data.renderer.RenderInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PlayerTracker extends EntityTracker {

    private final Player player;

    public PlayerTracker(@NotNull Player entity, @NotNull RenderInstance instance) {
        super(entity, instance);
        this.player = entity;
        instance.filter(p -> BetterModel.inst().playerManager().player(p).showPlayerLimb());
    }

    @Override
    public boolean isRunningSingleAnimation() {
        return false;
    }

    @Override
    public void close() throws Exception {
        super.close();
        player.updateInventory();
    }
}
