package com.soy.soycheese.skill;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class BaseSkill extends AbstractSkill {
    public BaseSkill(int type) {
        super(type);
    }
    public static BaseSkill.Builder builder(int type) {
        return new BaseSkill.Builder(type);
    }
    protected BaseSkill(BaseSkill.Builder builder) {
        this(builder.type);
    }
    @Override
    public Component getDescription(Player player)  {
        if(this.getIslock(player))
            return Component.translatable(getOrCreateNameid() + ".lock");
        return Component.translatable(getOrCreateNameid() + ".unlock");
    }

    @Override
    public boolean getIslock(Player player) {
        return true;
    }

    @Override
    public void onTick(Player player) {
    }

    @Override
    public void onEquip(Player player) {
    }

    @Override
    public void onUnequip(Player player) {
    }

    @Override
    public void onChangeOtherEquip(Player player, @Nullable AbstractSkill newskill, @Nullable AbstractSkill oldskill) {
    }
    public static class Builder {
        int type;
        public Builder(int type)
        {
            this.type = type;
        }
        public BaseSkill build() {
            return build(BaseSkill::new);
        }
        public BaseSkill build(java.util.function.Function<Builder, BaseSkill> builder) {
            return builder.apply(this);
        }
    }
}
