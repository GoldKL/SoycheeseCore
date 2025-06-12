package com.soy.soycheese.skill;


import com.soy.soycheese.kubejs.KubejsSkillBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class KubejsSkill extends AbstractSkill {
    private final BiConsumer<KubejsSkill,Player> onTick;
    private final BiConsumer<KubejsSkill,Player> onEquip;
    private final BiConsumer<KubejsSkill,Player> onUnequip;
    private final BiConsumer<KubejsSkill,SkillContext> onChangeOtherEquip;
    private final BiFunction<KubejsSkill,Player, Component> getDescription;
    private final BiFunction<KubejsSkill,Player, Boolean> getIslock;

    public KubejsSkill(int type,
                       BiConsumer<KubejsSkill,Player> onTick,
                       BiConsumer<KubejsSkill,Player> onEquip,
                       BiConsumer<KubejsSkill,Player> onUnequip,
                       BiConsumer<KubejsSkill,SkillContext> onChangeOtherEquip,
                       BiFunction<KubejsSkill,Player, Component> getDescription,
                       BiFunction<KubejsSkill,Player, Boolean> getIslock) {
        super(type);
        this.onTick = onTick;
        this.onEquip = onEquip;
        this.onUnequip = onUnequip;
        this.onChangeOtherEquip = onChangeOtherEquip;
        this.getDescription = getDescription;
        this.getIslock = getIslock;
    }
    public KubejsSkill(KubejsSkillBuilder builder) {
        this(builder.type, builder.onTick, builder.onEquip, builder.onUnequip, builder.onChangeOtherEquip, builder.getDescription, builder.getIslock);
    }
    protected KubejsSkill(KubejsSkill.Builder builder) {
        this(builder.type, builder.onTick, builder.onEquip, builder.onUnequip, builder.onChangeOtherEquip, builder.getDescription, builder.getIslock);
    }
    public static KubejsSkill.Builder builder(int type) {
        return new KubejsSkill.Builder(type);
    }
    public static class SkillContext {
        public final Player player;
        @Nullable
        public final AbstractSkill newskill;
        @Nullable
        public final AbstractSkill oldskill;
        SkillContext(Player player,@Nullable AbstractSkill newskill,@Nullable AbstractSkill oldskill) {
            this.player = player;
            this.newskill = newskill;
            this.oldskill = oldskill;
        }
    }
    @Override
    public Component getDescription(Player player) {
        return getDescription.apply(this,player);
    }

    @Override
    public boolean getIslock(Player player) {
        return getIslock.apply(this,player);
    }

    @Override
    public void onTick(Player player) {
        onTick.accept(this,player);
    }

    @Override
    public void onEquip(Player player) {
        onEquip.accept(this,player);
    }

    @Override
    public void onUnequip(Player player) {
        onUnequip.accept(this,player);
    }

    @Override
    public void onChangeOtherEquip(Player player, @Nullable AbstractSkill newskill, @Nullable AbstractSkill oldskill) {
        this.onChangeOtherEquip.accept(this, new SkillContext(player, newskill, oldskill));
    }
    public static class Builder {
        private BiConsumer<KubejsSkill,Player> onTick;
        private BiConsumer<KubejsSkill,Player> onEquip;
        private BiConsumer<KubejsSkill,Player> onUnequip;
        private BiConsumer<KubejsSkill,SkillContext> onChangeOtherEquip;
        private BiFunction<KubejsSkill,Player, Component> getDescription;
        private BiFunction<KubejsSkill,Player, Boolean> getIslock;
        int type;
        public Builder(int type)
        {
            this.type = type;
        }
        public Builder onTick(BiConsumer<KubejsSkill,Player> onTick) {
            this.onTick = onTick;
            return this;
        }
        public Builder onEquip(BiConsumer<KubejsSkill,Player> onEquip) {
            this.onEquip = onEquip;
            return this;
        }
        public Builder onUnequip(BiConsumer<KubejsSkill,Player> onUnequip) {
            this.onUnequip = onUnequip;
            return this;
        }
        public Builder onChangeOtherEquip(BiConsumer<KubejsSkill,SkillContext> onChangeOtherEquip) {
            this.onChangeOtherEquip = onChangeOtherEquip;
            return this;
        }
        public Builder getDescription(BiFunction<KubejsSkill,Player,Component> getDescription) {
            this.getDescription = getDescription;
            return this;
        }
        public Builder getIslock(BiFunction<KubejsSkill,Player,Boolean> getIslock) {
            this.getIslock = getIslock;
            return this;
        }
        public KubejsSkill build() {
            if(this.onTick == null)
                this.onTick = (kubejsSkill, player) -> {};
            if(this.onEquip == null)
                this.onEquip = (kubejsSkill, player) -> {};
            if(this.onUnequip == null)
                this.onUnequip = (kubejsSkill, player) -> {};
            if(this.onChangeOtherEquip == null)
                this.onChangeOtherEquip = (kubejsSkill, player) -> {};
            if(this.getDescription == null)
                this.getDescription = (kubejsSkill,player) ->{
                    if(kubejsSkill.getIslock(player))
                        return Component.translatable(kubejsSkill.getOrCreateNameid() + ".lock");
                    return Component.translatable(kubejsSkill.getOrCreateNameid() + ".unlock");
                };
            if(this.getIslock == null)
                this.getIslock = (kubejsSkill,player) -> true;
            return build(KubejsSkill::new);
        }
        public KubejsSkill build(java.util.function.Function<KubejsSkill.Builder, KubejsSkill> builder) {
            return builder.apply(this);
        }
    }
}
