package com.soy.soycheese.kubejs;

import com.soy.soycheese.skill.BaseSkill;
import com.soy.soycheese.skill.KubejsSkill;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class KubejsSkillBuilder extends BuilderBase<KubejsSkill> {
    public KubejsSkillBuilder(ResourceLocation i) {
        super(i);
        this.type = 0;
    }
    public BiConsumer<KubejsSkill,Player> onTick;
    public BiConsumer<KubejsSkill,Player> onEquip;
    public BiConsumer<KubejsSkill,Player> onUnequip;
    public BiConsumer<KubejsSkill, KubejsSkill.SkillContext> onChangeOtherEquip;
    public BiFunction<KubejsSkill,Player, Component> getDescription;
    public BiFunction<KubejsSkill,Player, Boolean> getIslock;
    public BiFunction<KubejsSkill, KubejsSkill.AttackContext, Boolean> onAttack;
    public BiFunction<KubejsSkill, KubejsSkill.AttackedContext,Boolean> onAttacked;
    public BiFunction<KubejsSkill, KubejsSkill.HurtContext,Float> onHurt;
    public BiFunction<KubejsSkill, KubejsSkill.HurtedContext,Float> onHurted;
    public BiFunction<KubejsSkill, KubejsSkill.HurtContext,Float> onDamage;
    public BiFunction<KubejsSkill, KubejsSkill.HurtedContext,Float> onDamaged;
    public final LinkedHashMap<String,Object> skill_arguments = new LinkedHashMap<>();
    public final HashMap<String,Class<?>> skill_types = new HashMap<>();
    public final HashMap<String,Object> skill_defaults = new HashMap<>();
    public int type;
    @Info("Set the skill's argument")
    public <T> KubejsSkillBuilder initSkillArgument(String name, T value) {
        if(BaseSkill.isAcceptType(value))
        {
            this.skill_arguments.put(name, value);
            this.skill_defaults.put(name, value);
            this.skill_types.put(name,value.getClass());
        }
        else
            throw new IllegalArgumentException("Couldn't accept argument" + name + "'s type");
        return this;
    }
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
    @Info("Set player when player attacks")
    public KubejsSkillBuilder onAttack(BiFunction<KubejsSkill, KubejsSkill.AttackContext, Boolean> onAttack) {
        this.onAttack = onAttack;
        return this;
    }
    @Info("Set player when player is attacked")
    public KubejsSkillBuilder onAttacked(BiFunction<KubejsSkill, KubejsSkill.AttackedContext, Boolean> onAttacked) {
        this.onAttacked = onAttacked;
        return this;
    }
    @Info("Set player when player hurts")
    public KubejsSkillBuilder onHurt(BiFunction<KubejsSkill, KubejsSkill.HurtContext, Float> onHurt) {
        this.onHurt = onHurt;
        return this;
    }
    @Info("Set player when player is hurt")
    public KubejsSkillBuilder onHurted(BiFunction<KubejsSkill, KubejsSkill.HurtedContext, Float> onHurted) {
        this.onHurted = onHurted;
        return this;
    }
    @Info("Set player when player damages")
    public KubejsSkillBuilder onDamage(BiFunction<KubejsSkill, KubejsSkill.HurtContext, Float> onDamage) {
        this.onDamage = onDamage;
        return this;
    }
    @Info("Set player when player is damaged")
    public KubejsSkillBuilder onDamaged(BiFunction<KubejsSkill, KubejsSkill.HurtedContext, Float> onDamaged) {
        this.onDamaged = onDamaged;
        return this;
    }
    @Override
    public RegistryInfo getRegistryType() {
        return SkillPlugin.SKILLS;
    }

    @Override
    public KubejsSkill createObject() {
        return new KubejsSkill(this);
    }
}
