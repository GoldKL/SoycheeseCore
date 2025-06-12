package com.soy.soycheese.kubejs;

import com.soy.soycheese.skill.KubejsSkill;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class KubejsSkillBuilder extends BuilderBase<KubejsSkill> {
    public KubejsSkillBuilder(ResourceLocation i) {
        super(i);
        this.type = 0;
    }
    public BiConsumer<KubejsSkill, Player> onTick;
    public BiConsumer<KubejsSkill,Player> onEquip;
    public BiConsumer<KubejsSkill,Player> onUnequip;
    public BiConsumer<KubejsSkill, KubejsSkill.SkillContext> onChangeOtherEquip;
    public BiFunction<KubejsSkill,Player, Component> getDescription;
    public BiFunction<KubejsSkill,Player, Boolean> getIslock;
    public int type;
    @Info("Set the skill's type")
    public KubejsSkillBuilder type(int type) {
        this.type = type;
        return this;
    }
    @Info("Set the skill's onTick event")
    public KubejsSkillBuilder onTick(BiConsumer<KubejsSkill,Player> onTick) {
        this.onTick = onTick;
        return this;
    }
    @Info("Set the skill's onEquip event")
    public KubejsSkillBuilder onEquip(BiConsumer<KubejsSkill,Player> onEquip) {
        this.onEquip = onEquip;
        return this;
    }
    @Info("Set the skill's onUnequip event")
    public KubejsSkillBuilder onUnequip(BiConsumer<KubejsSkill,Player> onUnequip) {
        this.onUnequip = onUnequip;
        return this;
    }
    @Info("Set the skill's onChangeOtherEquip event")
    public KubejsSkillBuilder onChangeOtherEquip(BiConsumer<KubejsSkill, KubejsSkill.SkillContext> onChangeOtherEquip) {
        this.onChangeOtherEquip = onChangeOtherEquip;
        return this;
    }
    @Info("Set the skill's Description")
    public KubejsSkillBuilder getDescription(BiFunction<KubejsSkill,Player,Component> getDescription) {
        this.getDescription = getDescription;
        return this;
    }
    @Info("Set player how to unlock this skill, true is lock")
    public KubejsSkillBuilder getIslock(BiFunction<KubejsSkill,Player,Boolean> getIslock) {
        this.getIslock = getIslock;
        return this;
    }
    @Override
    public RegistryInfo getRegistryType() {
        return SkillPlugin.SKILLS;
    }

    @Override
    public KubejsSkill createObject() {
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
        return new KubejsSkill(this);
    }
}
